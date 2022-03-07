package io.wispforest.jello.main.common.blocks;

import io.wispforest.jello.api.dye.DyeColorant;

public interface DyeableBlock {
    int getBlockColor();

    DyeColorant getDyeColor();
}
