package io.wispforest.jello.compat.condensedCreative;

import io.wispforest.condensed_creative.registry.CondensedCreativeInitializer;
import io.wispforest.condensed_creative.registry.CondensedEntryRegistry;
import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_entries.DyeEntriesInit;
import io.wispforest.gelatin.dye_entries.compat.condensedCreative.GelatinCCEntrypoint;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_entries.variants.item.DyeableItemVariant;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import io.wispforest.jello.Jello;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.misc.JelloBlockVariants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JelloCCEntrypoint implements CondensedCreativeInitializer {

    @Override
    public void onInitializeCondensedEntries(boolean refreshed) {
        CondensedEntryRegistry.fromBlockTag(Jello.id("slime_slabs"), JelloBlocks.SLIME_SLAB, JelloBlockVariants.SLIME_SLAB.getPrimaryBlockTag())
                .setEntrySorting(allStacks -> GelatinCCEntrypoint.sortItemStacks(allStacks, JelloBlocks.SLIME_SLAB.asItem(),
                        stack -> ((DyeBlockStorage)((BlockItem)stack.getItem()).getBlock()).getDyeColorant()))
                .setTitleString(Text.translatable("slime_slabs_condensed"))
                .addItemGroup(ItemGroup.REDSTONE);

        //--------------------

        CondensedEntryRegistry.fromBlockTag(Jello.id("slime_blocks"), Blocks.SLIME_BLOCK, JelloBlockVariants.SLIME_BLOCK.getPrimaryBlockTag())
                .setEntrySorting(allStacks -> GelatinCCEntrypoint.sortItemStacks(allStacks, Blocks.SLIME_BLOCK.asItem(),
                        stack -> ((DyeBlockStorage)((BlockItem)stack.getItem()).getBlock()).getDyeColorant()))
                .setTitleString(Text.translatable("slime_blocks_condensed"))
                .addItemGroup(ItemGroup.REDSTONE);

    }
}
