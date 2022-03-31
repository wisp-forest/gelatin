package io.wispforest.jello.misc.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;

public interface DyeItemStorage {

    default DyeColorant getDyeColorant() {
        return DyeColorantRegistry.NULL_VALUE_NEW;
    }

    default void setDyeColor(DyeColorant dyeColorant) {

    }
}
