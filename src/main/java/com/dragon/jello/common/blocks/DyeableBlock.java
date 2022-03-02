package com.dragon.jello.common.blocks;

import com.dragon.jello.dyelib.DyeColorRegistry;

public interface DyeableBlock {
    int getBlockColor();

    DyeColorRegistry.DyeColor getDyeColor();
}
