package io.wispforest.dye_registry.ducks;

import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.mixins.BlockMixin;
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
