package io.wispforest.jello.api.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.mixin.dye.BlockMixin;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link BlockMixin} to have a bases for other Blocks to have storage for {@link DyeColorant}
 */
public interface DyeBlockStorage {

    /**
     * Get the {@link DyeColorant} of a DyeBlockStorage
     */
    DyeColorant getDyeColorant();

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
