package io.wispforest.dye_registry.ducks;

import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface DyeBlockEntityStorage {

    /**
     * Get the DyeColorant from a Block
     * @return Will return {@link DyeColorantRegistry#NULL_VALUE_NEW}
     */
    DyeColorant getDyeColor();

    /**
     * Method used to set the DyeColorant of a DyeBlockEntityStorage
     */
    void setDyeColor(DyeColorant dyeColorant);

}
