package io.wispforest.gelatin.common.util;

import io.wispforest.gelatin.common.mixins.SettingsAccessor;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.ops.ItemOps;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ItemFunctions {

    public static void decrementPlayerHandItemCC(PlayerEntity player, Hand hand) {
        if (!player.getAbilities().creativeMode) ItemOps.decrementPlayerHandItem(player, hand);
    }

    /**
     *
     * Method used to make a copy of a given {@link Item.Settings}.
     * Will not copy any fabric or quilted custom Item.Settings data
     *
     * @param settings The Settings to copy from
     * @return A 1:1 Deep copy of the given {@link Item.Settings} as {@link OwoItemSettings}
     */
    public static OwoItemSettings copyFrom(Item.Settings settings){
        OwoItemSettings settingsNew = new OwoItemSettings();

        if(settings instanceof OwoItemSettings oldOwoItemSettings){
            settingsNew.tab(oldOwoItemSettings.tab());
        }

        SettingsAccessor settingsAccessor = (SettingsAccessor) settings;

        if (settingsAccessor.gelatin$isFireproof()) settingsNew.fireproof();

        settingsNew
                .food(settingsAccessor.gelatin$getFoodComponent())
                .recipeRemainder(settingsAccessor.gelatin$getRecipeRemainder())
                .rarity(settingsAccessor.gelatin$getRarity());

        if(settingsAccessor.gelatin$getMaxDamage() > 0){
            settingsNew.maxDamageIfAbsent(settingsAccessor.gelatin$getMaxDamage());
        } else {
            settingsNew.maxCount(settingsAccessor.gelatin$getMaxCount());
        }

        return settingsNew;
    }

    public static Identifier getIdFromConvertible(ItemConvertible itemConvertible){
        if(itemConvertible.asItem() != Blocks.AIR.asItem()){
            return Registries.ITEM.getId(itemConvertible.asItem());
        }

        return Registries.BLOCK.getId((Block) itemConvertible);
    }
}
