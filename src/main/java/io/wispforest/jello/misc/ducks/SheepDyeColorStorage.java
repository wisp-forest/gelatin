package io.wispforest.jello.misc.ducks;

import io.wispforest.jello.api.dye.DyeColorant;

public interface SheepDyeColorStorage {

    DyeColorant getWoolDyeColor();

    void setWoolDyeColor(DyeColorant dyeColorant);
}
