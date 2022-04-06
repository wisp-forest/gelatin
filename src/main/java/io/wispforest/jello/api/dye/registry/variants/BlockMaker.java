package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.DyeColorant;
import net.minecraft.block.Block;

import javax.annotation.Nullable;

public interface BlockMaker {
    Block createBlockFromDyeColor(DyeColorant dyeColorant, @Nullable Block parentBlock);
}
