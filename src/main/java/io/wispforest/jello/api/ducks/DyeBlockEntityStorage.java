package io.wispforest.jello.api.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import org.jetbrains.annotations.ApiStatus;

public interface DyeBlockEntityStorage {

    DyeColorant getDyeColor();

    void setDyeColor(DyeColorant dyeColorant);

}
