package io.wispforest.gelatin.dye_registry.ducks;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import org.jetbrains.annotations.ApiStatus;

public interface DyeStorage {

    /**
     * This will return the {@link DyeColorant} of the given Object
     */
    default DyeColorant getDyeColorant(){
        return DyeColorantRegistry.NULL_VALUE_NEW;
    }

    default boolean isDyed() {
        return getDyeColorant() != DyeColorantRegistry.NULL_VALUE_NEW;
    }

    //------------------------------------------

    /**
     * [Warning]: Advise not changing this after the fact and only for initilizing the {@link DyeColorant}
     */
    @ApiStatus.NonExtendable
    default void setDyeColor(DyeColorant dyeColorant){};
}
