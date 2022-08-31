package io.wispforest.gelatin.dye_entries.variants.block;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import net.minecraft.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Used internally within {@link DyeableBlockVariant#makeChildBlock} to create variant blocks.
 */
@ApiStatus.NonExtendable
public interface BlockMaker {
    Block createBlockFromDyeColor(DyeColorant dyeColorant, @Nullable Block parentBlock);
}
