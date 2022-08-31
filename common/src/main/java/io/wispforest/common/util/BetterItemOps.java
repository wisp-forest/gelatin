package io.wispforest.common.util;

import io.wispforest.common.mixins.SettingsAccessor;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.ops.ItemOps;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BetterItemOps {

    public static void decrementPlayerHandItemCC(PlayerEntity player, Hand hand) {
        if (!player.getAbilities().creativeMode)
            ItemOps.decrementPlayerHandItem(player, hand);
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
            settingsNew.tab(oldOwoItemSettings.getTab());
        }

        SettingsAccessor settingsAccessor = (SettingsAccessor) settings;

        if (settingsAccessor.jello$isFireproof()) {
            settingsNew.fireproof();
        }

        settingsNew.group(settingsAccessor.jello$getGroup())
                .food(settingsAccessor.jello$getFoodComponent())
                .recipeRemainder(settingsAccessor.jello$getRecipeRemainder())
                .rarity(settingsAccessor.jello$getRarity());

        if(settingsAccessor.jello$getMaxDamage() > 0){
            settingsNew.maxDamageIfAbsent(settingsAccessor.jello$getMaxDamage());
        } else {
            settingsNew.maxCount(settingsAccessor.jello$getMaxCount());
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
