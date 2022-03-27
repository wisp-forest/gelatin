package io.wispforest.jello.api.mixin.ducks;

import io.wispforest.jello.api.dye.DyeColorant;

public interface SheepDyeColorStorage {

    DyeColorant getWoolDyeColor();

    void setWoolDyeColor(DyeColorant dyeColorant);
}
