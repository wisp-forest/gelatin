package io.wispforest.jello.compat.condensedCreative;

import io.wispforest.condensed_creative.registry.CondensedCreativeInitializer;
import io.wispforest.condensed_creative.registry.CondensedEntryRegistry;
import io.wispforest.gelatin.dye_entries.compat.condensedCreative.GelatinCCEntrypoint;
import io.wispforest.jello.Jello;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.misc.JelloBlockVariants;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
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
                .setTitleString(Text.translatable("slime_balls_condensed"))
                .addItemGroup(ItemGroup.MISC);


        CondensedEntryRegistry.fromBlockTag(Jello.id("slime_slabs"), JelloBlocks.SLIME_SLAB, JelloBlockVariants.SLIME_SLAB.getPrimaryTag())
                .setEntrySorting(allStacks -> {
                    GelatinCCEntrypoint.sortItemStacks(allStacks, JelloBlocks.SLIME_SLAB.asItem(),
                            stack -> stack.getItem().getDyeColorant());
                })
                .setTitleString(Text.translatable("slime_slabs_condensed"))
                .addItemGroup(ItemGroup.REDSTONE);

        //--------------------

        CondensedEntryRegistry.fromBlockTag(Jello.id("slime_blocks"), Blocks.SLIME_BLOCK, JelloBlockVariants.SLIME_BLOCK.getPrimaryTag())
                .setEntrySorting(allStacks -> {
                    GelatinCCEntrypoint.sortItemStacks(allStacks, Blocks.SLIME_BLOCK.asItem(),
                            stack -> ((BlockItem)stack.getItem()).getBlock().getDyeColorant());
                })
                .setTitleString(Text.translatable("slime_blocks_condensed"))
                .addItemGroup(ItemGroup.REDSTONE);

    }
}
