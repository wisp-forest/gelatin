package io.wispforest.gelatin.common.util;

import io.wispforest.gelatin.common.compat.owo.OwoCompatItemSettings;
import io.wispforest.gelatin.common.mixins.SettingsAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemFunctions {

    public static Supplier<Item.Settings> itemSettingsConstructor = Item.Settings::new;

    public static final List<BiConsumer<Item.Settings, Item.Settings>> copyMethods = new ArrayList<>();

    static {
        if(FabricLoader.getInstance().isModLoaded("owo")) OwoCompatItemSettings.init();
    }

    public static void decrementPlayerHandItemCC(PlayerEntity player, Hand hand) {
        if (!player.getAbilities().creativeMode) decrementPlayerHandItem(player, hand);
    }

    /**
     * Copy of owo Function: <a href="https://github.com/wisp-forest/owo-lib/blob/8560ec4b32d8752785d919ce8db1cc0c057c1b8a/src/main/java/io/wispforest/owo/ops/ItemOps.java#L88C7-L88C7">Link</a>
     */
    public static boolean decrementPlayerHandItem(PlayerEntity player, Hand hand) {
        var stack = player.getStackInHand(hand);
        if (!player.isCreative()) {
            if (!emptyAwareDecrement(stack)) player.setStackInHand(hand, ItemStack.EMPTY);
        }
        return !stack.isEmpty();
    }

    /**
     * Copy of owo Function: <a href="https://github.com/wisp-forest/owo-lib/blob/8560ec4b32d8752785d919ce8db1cc0c057c1b8a/src/main/java/io/wispforest/owo/ops/ItemOps.java#L75C5-L75C5">Link</a>
     */
    public static boolean emptyAwareDecrement(ItemStack stack) {
        stack.decrement(1);
        return !stack.isEmpty();
    }

    /**
     * Method used to make a copy of a given {@link Item.Settings}.
     * Will not copy any fabric or quilted custom Item.Settings data
     *
     * @param settings The Settings to copy from
     * @return A 1:1 Deep copy of the given {@link Item.Settings}
     */
    public static Item.Settings copyFrom(Item.Settings settings){
        Item.Settings settingsNew = itemSettingsConstructor.get();

        copyMethods.forEach(copyMethod -> copyMethod.accept(settingsNew, settings));

        SettingsAccessor settingsAccessor = (SettingsAccessor) settings;

        if (settingsAccessor.gelatin$isFireproof()) settingsNew.fireproof();

        settingsNew
                .group(settingsAccessor.gelatin$getGroup())
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
            return Registry.ITEM.getId(itemConvertible.asItem());
        }

        return Registry.BLOCK.getId((Block) itemConvertible);
    }
}
