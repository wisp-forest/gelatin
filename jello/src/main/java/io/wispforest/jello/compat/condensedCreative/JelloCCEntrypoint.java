package io.wispforest.jello.compat.condensedCreative;

import io.wispforest.condensed_creative.registry.CondensedCreativeInitializer;
import io.wispforest.condensed_creative.registry.CondensedEntryRegistry;
import io.wispforest.gelatin.dye_entries.compat.condensedCreative.GelatinCCEntrypoint;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import io.wispforest.jello.Jello;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.misc.JelloBlockVariants;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class JelloCCEntrypoint implements CondensedCreativeInitializer {

    @Override
    public void onInitializeCondensedEntries(boolean refreshed) {
        List<Item> slimeballs = new ArrayList<>(List.of(Items.SLIME_BALL));

        slimeballs.addAll(JelloItems.Slimeballs.SLIME_BALLS);

        CondensedEntryRegistry.fromItems(Jello.id("slime_balls"), Items.SLIME_BALL, slimeballs)
                .setEntrySorting(allStacks -> {
                    GelatinCCEntrypoint.sortItemStacks(allStacks, Items.SLIME_BALL,
                            stack -> stack.getItem().getDyeColorant());
                })
                .setTitleSupplier(() -> Text.translatable("slime_balls_condensed"))
                .addToItemGroup(ItemGroups.INGREDIENTS);


        CondensedEntryRegistry.fromTag(Jello.id("slime_slabs"), JelloBlocks.SLIME_SLAB, JelloBlockVariants.SLIME_SLAB.getPrimaryTag())
                .setEntrySorting(allStacks -> {
                    GelatinCCEntrypoint.sortItemStacks(allStacks, JelloBlocks.SLIME_SLAB.asItem(),
                            stack -> ((DyeItemStorage) stack.getItem()).getDyeColorant());
                })
                .setTitleSupplier(() -> Text.translatable("slime_slabs_condensed"))
                .addToItemGroup(ItemGroups.REDSTONE);

        //--------------------

        CondensedEntryRegistry.fromTag(Jello.id("slime_blocks"), Blocks.SLIME_BLOCK, JelloBlockVariants.SLIME_BLOCK.getPrimaryTag())
                .setEntrySorting(allStacks -> {
                    GelatinCCEntrypoint.sortItemStacks(allStacks, Blocks.SLIME_BLOCK.asItem(),
                            stack -> ((DyeBlockStorage)((BlockItem)stack.getItem()).getBlock()).getDyeColorant());
                })
                .setTitleSupplier(() -> Text.translatable("slime_blocks_condensed"))
                .addToItemGroup(ItemGroups.REDSTONE);

    }
}
