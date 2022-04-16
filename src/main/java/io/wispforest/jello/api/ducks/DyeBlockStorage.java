package io.wispforest.jello.api.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import org.jetbrains.annotations.ApiStatus;

public interface DyeBlockStorage {

    DyeColorant getDyeColor();

    /**
     * Method to check if a block is currently Dyed or not
     */
    boolean isBlockDyed();

    //---------------------------------------------

    /**
     * [Warning]: Advise not changing this after the fact and only for initilizing the {@link DyeColorant}
     */
    @ApiStatus.NonExtendable
    default void setDyeColor(DyeColorant dyeColorant){};

}
