package io.wispforest.jello.api.mixin.ducks;

import io.wispforest.jello.api.dye.DyeColorant;

public interface DyeBlockStorage {

    void setDyeColor(DyeColorant dyeColorant);

    DyeColorant getDyeColor();

    boolean isBlockDyed();
}
