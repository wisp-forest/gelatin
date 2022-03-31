package io.wispforest.jello.misc.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;

public interface DyeItemStorage {

    default DyeColorant getDyeColor() {
        return DyeColorantRegistry.NULL_VALUE_NEW;
    }

    default void setDyeColor(DyeColorant dyeColorant) {

    }

    ;

}
