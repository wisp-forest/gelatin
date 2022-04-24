package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.item.ColoredBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used internally within {@link DyeableBlockVariant#makeBlockItem(DyeColorant, Block, Item.Settings)} to create variant block items (If the variant has such).
 * <p>Should not be extended</p>
 */
@ApiStatus.NonExtendable
public interface BlockItemMaker {
    BlockItemMaker DEFAULT = (dyeColorant, block, settings) -> new ColoredBlockItem(block, settings);

    BlockItem createBlockItemFromDyeColor(DyeColorant dyeColorant, Block block, Item.Settings settings);
}
