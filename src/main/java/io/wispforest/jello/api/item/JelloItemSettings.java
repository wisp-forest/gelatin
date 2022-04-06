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

import java.util.Map;

public class JelloItemSettings extends OwoItemSettings {

    public void addRecipeSpecificRemainder(Identifier recipeID, Item recipeRemainder) {
        ((JelloItemSettingsExtensions) this).addRecipeSpecificRemainder(recipeID, recipeRemainder);
    }

    public Map<Identifier, Item> getRecipeSpecificRemainders() {
        return ((JelloItemSettingsExtensions) this).getRecipeSpecificRemainder();
    }

    public static OwoItemSettings copyFrom(Item.Settings settings) {
        OwoItemSettings settingsNew = new OwoItemSettings();

        SettingsAccessor settingsAccessor = (SettingsAccessor) settings;

        if (((SettingsAccessor) settings).isFireproof()) {
            settingsNew.fireproof();
        }

        settingsNew.group(settingsAccessor.getGroup())
                .food(settingsAccessor.getFoodComponent())
                .recipeRemainder(settingsAccessor.getRecipeRemainder())
                .maxCount(settingsAccessor.getMaxCount())
                .maxDamageIfAbsent(settingsAccessor.getMaxDamage())
                .rarity(settingsAccessor.getRarity());

        if (settings instanceof FabricItemSettings) {
            ExtraDataAccessor oldData = (ExtraDataAccessor) (Object) FabricItemInternalsAccessor.getExtraData().get(settings);

            if (oldData != null) {
                settingsNew.customDamage(oldData.getCustomDamageHandler());
                settingsNew.equipmentSlot(oldData.getEquipmentSlotProvider());
            }

            if (settings instanceof OwoItemSettings owoItemSettings) {
                settingsNew.tab(owoItemSettings.getTab());
            }
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
