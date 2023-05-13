package io.wispforest.gelatin.dye_entries.compat.condensedCreative;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.condensed_creative.registry.CondensedCreativeInitializer;
import io.wispforest.condensed_creative.registry.CondensedEntryRegistry;
import io.wispforest.gelatin.dye_entries.DyeEntriesInit;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantRegistry;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GelatinCCEntrypoint implements CondensedCreativeInitializer {

    @Override
    public void onInitializeCondensedEntries(boolean refreshed) {
        DyeableVariantRegistry.getAllBlockVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.alwaysReadOnly() && dyeableBlockVariant.createBlockItem()).forEach(dyeableBlockVariant -> {
            CondensedEntryRegistry.fromBlockTag(dyeableBlockVariant.variantIdentifier, dyeableBlockVariant.getDefaultEntry(), dyeableBlockVariant.getPrimaryTag())
                .setTitleString(Text.translatable(dyeableBlockVariant.variantIdentifier.getPath() + "_condensed"))
                .setEntrySorting(allStacks -> sortItemStacks(allStacks, dyeableBlockVariant.getDefaultBlockItem(),
                    stack -> ((DyeBlockStorage)((BlockItem)stack.getItem()).getBlock()).getDyeColorant()))
                .addItemGroup(DyeEntriesInit.MAIN_ITEM_GROUP, 1);

        });

        DyeableVariantRegistry.getAllItemVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.alwaysReadOnly()).forEach(dyeableItemVariant -> {
            CondensedEntryRegistry.fromItemTag(dyeableItemVariant.variantIdentifier, dyeableItemVariant.getDefaultEntry(), dyeableItemVariant.getPrimaryTag())
                    .setTitleString(Text.translatable(dyeableItemVariant.variantIdentifier.getPath() + "_condensed"))
                    .setEntrySorting(allStacks -> sortItemStacks(allStacks, dyeableItemVariant.getDefaultEntry(), stack -> ((DyeItemStorage)stack.getItem()).getDyeColorant()))
                    .addItemGroup(DyeEntriesInit.MAIN_ITEM_GROUP, 0);

        });
    }

    public static void sortItemStacks(List<ItemStack> allStacks, Item defaultItem, Function<ItemStack, DyeColorant> getColorFunc){
        Predicate<ItemStack> getNonVanillaItem = stack -> !DyeColorantRegistry.Constants.VANILLA_DYES.contains(getColorFunc.apply(stack));

        List<ItemStack> nonVanillaStacks = allStacks.stream().filter(getNonVanillaItem).collect(Collectors.toList());
        allStacks.removeIf(getNonVanillaItem);

        nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
            float[] hsl = ColorUtil.rgbToHsl(getColorFunc.apply(stack).getBaseColor());

            return hsl[2];
        }));

        nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
            float[] hsl = ColorUtil.rgbToHsl(getColorFunc.apply(stack).getBaseColor());

            return hsl[1];
        }));

        nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
            float[] hsl = ColorUtil.rgbToHsl(getColorFunc.apply(stack).getBaseColor());

            return hsl[0];
        }));

        allStacks.addAll(nonVanillaStacks);

        if(getColorFunc.apply(defaultItem.getDefaultStack()) != DyeColorantRegistry.WHITE){
            allStacks.add(0, defaultItem.getDefaultStack());
        }
    }
}
