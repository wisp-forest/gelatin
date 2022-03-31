package io.wispforest.jello.misc.ducks;

import io.wispforest.jello.api.dye.DyeColorant;

public interface DyeBlockStorage {

    void setDyeColor(DyeColorant dyeColorant);

    DyeColorant getDyeColor();

    boolean isBlockDyed();
}
