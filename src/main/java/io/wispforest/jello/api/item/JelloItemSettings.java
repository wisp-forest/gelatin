package io.wispforest.jello.api.item;

import io.wispforest.jello.misc.ducks.JelloItemSettingsExtensions;
import io.wispforest.jello.mixin.accessors.FabricItemInternalsAccessor;
import io.wispforest.jello.mixin.accessors.FabricItemInternalsAccessor.ExtraDataAccessor;
import io.wispforest.jello.mixin.accessors.SettingsAccessor;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Experimental
public class JelloItemSettings extends OwoItemSettings {

    public void addRecipeSpecificRemainder(Identifier recipeID, Item recipeRemainder) {
        ((JelloItemSettingsExtensions) this).addRecipeSpecificRemainder(recipeID, recipeRemainder);
    }

    public Map<Identifier, Item> getRecipeSpecificRemainders() {
        return ((JelloItemSettingsExtensions) this).getRecipeSpecificRemainder();
    }

    /**
     * Method to make an exact copy of {@link Item.Settings} given and will keep the level of Settings whether {@link OwoItemSettings} or {@link FabricItemSettings}
     *
     * @param settingsOld Origin Settings
     * @return a full copy of the given settings
     */
    public static Item.Settings copyFrom(Item.Settings settingsOld) {

        Item.Settings settingsNew = null;

        if(settingsOld instanceof OwoItemSettings oldOwoItemSettings){
            settingsNew = new OwoItemSettings();

            ((OwoItemSettings)settingsNew).tab(oldOwoItemSettings.getTab());
        }

        if(settingsOld instanceof FabricItemSettings oldFabricItemSettings){
            if(settingsNew == null) {
                settingsNew = new FabricItemSettings();
            }

            ExtraDataAccessor oldData = (ExtraDataAccessor) (Object) FabricItemInternalsAccessor.getExtraData().get(oldFabricItemSettings);

            if (oldData != null) {
                ((FabricItemSettings)settingsNew).customDamage(oldData.getCustomDamageHandler());
                ((FabricItemSettings)settingsNew).equipmentSlot(oldData.getEquipmentSlotProvider());
            }
        }

        if(settingsNew == null){
            settingsNew = new Item.Settings();
        }

        SettingsAccessor settingsAccessor = (SettingsAccessor) settingsOld;

        if (settingsAccessor.isFireproof()) {
            settingsNew.fireproof();
        }

        settingsNew.group(settingsAccessor.getGroup())
                .food(settingsAccessor.getFoodComponent())
                .recipeRemainder(settingsAccessor.getRecipeRemainder())
                .rarity(settingsAccessor.getRarity());

        if(settingsAccessor.getMaxDamage() > 0){
            settingsNew.maxDamageIfAbsent(settingsAccessor.getMaxDamage());
        } else {
            settingsNew.maxCount(settingsAccessor.getMaxCount());
        }

        return settingsNew;
    }

    public static Identifier getIdFromConvertible(ItemConvertible itemConvertible){
        if(itemConvertible.asItem() != Blocks.AIR.asItem()){
            return Registry.ITEM.getId(itemConvertible.asItem());
        }

        return Registry.BLOCK.getId((Block) itemConvertible);
    }
}
