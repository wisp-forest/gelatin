package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.item.ColoredBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public interface BlockItemMaker {
    BlockItemMaker DEFAULT = (dyeColorant, block, settings) -> new ColoredBlockItem(block, settings);

    BlockItem createBlockItemFromDyeColor(DyeColorant dyeColorant, Block block, Item.Settings settings);
}
