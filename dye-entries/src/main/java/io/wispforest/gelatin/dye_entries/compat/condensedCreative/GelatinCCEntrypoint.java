package io.wispforest.gelatin.dye_entries.compat.condensedCreative;

import io.wispforest.condensed_creative.registry.CondensedCreativeInitializer;
import io.wispforest.condensed_creative.registry.CondensedEntryRegistry;
import io.wispforest.gelatin.dye_entries.misc.DyeEntriesItemGroups;
import io.wispforest.gelatin.dye_entries.utils.DyeSortUtil;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantRegistry;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GelatinCCEntrypoint implements CondensedCreativeInitializer {

    @Override
    public void onInitializeCondensedEntries(boolean refreshed) {
        boolean owoEnabled = !DyeEntriesItemGroups.createSeparateGroups;

        ItemGroup group1 = DyeEntriesItemGroups.getItemGroup.apply(1);
        ItemGroup group2 = DyeEntriesItemGroups.getItemGroup.apply(0);

        DyeableVariantRegistry.getAllItemVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.alwaysReadOnly()).forEach(dyeableItemVariant -> {
            CondensedEntryRegistry.fromTag(dyeableItemVariant.variantIdentifier, dyeableItemVariant.getDefaultEntry(), dyeableItemVariant.getPrimaryTag())
                    .setTitleSupplier(() -> Text.translatable(dyeableItemVariant.variantIdentifier.getPath() + "_condensed"))
                    .setEntrySorting(allStacks -> sortItemStacks(allStacks, dyeableItemVariant.getDefaultEntry(), stack -> stack.getItem().getDyeColorant()))
                    .addToItemGroup(group1, owoEnabled ? 0 : -1);
        });

        DyeableVariantRegistry.getAllBlockVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.alwaysReadOnly() && dyeableBlockVariant.createBlockItem()).forEach(dyeableBlockVariant -> {
            CondensedEntryRegistry.fromTag(dyeableBlockVariant.variantIdentifier, dyeableBlockVariant.getDefaultEntry(), dyeableBlockVariant.getPrimaryTag())
                .setTitleSupplier(() -> Text.translatable(dyeableBlockVariant.variantIdentifier.getPath() + "_condensed"))
                .setEntrySorting(allStacks -> sortItemStacks(allStacks, dyeableBlockVariant.getDefaultBlockItem(),
                    stack -> ((BlockItem)stack.getItem()).getBlock().getDyeColorant()))
                .addToItemGroup(group2, owoEnabled ? 1 : -1);
        });
    }

    public static void sortItemStacks(List<ItemStack> allStacks, Item defaultItem, Function<ItemStack, DyeColorant> getColorFunc){
        Predicate<ItemStack> getNonVanillaItem = stack -> !DyeColorantRegistry.Constants.VANILLA_DYES.contains(getColorFunc.apply(stack));

        List<ItemStack> nonVanillaStacks = allStacks.stream().filter(getNonVanillaItem).collect(Collectors.toList());

        allStacks.removeIf(getNonVanillaItem);

        DyeSortUtil.sortColoredStacks(nonVanillaStacks, getColorFunc);

        allStacks.addAll(nonVanillaStacks);

        if(getColorFunc.apply(defaultItem.getDefaultStack()) != DyeColorantRegistry.WHITE){
            allStacks.add(0, defaultItem.getDefaultStack());
        }
    }
}
