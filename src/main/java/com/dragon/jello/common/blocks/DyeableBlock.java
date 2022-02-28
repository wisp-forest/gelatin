package com.dragon.jello.common.blocks;

import com.dragon.jello.lib.dyecolor.DyeColorRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.DyeColor;

public interface DyeableBlock {
    int getBlockColor();

    DyeColorRegistry.DyeColor getDyeColor();
}
