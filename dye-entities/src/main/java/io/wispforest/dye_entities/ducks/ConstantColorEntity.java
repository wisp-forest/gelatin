package io.wispforest.dye_entities.ducks;

import io.wispforest.dye_entities.misc.DataConstants;
import net.minecraft.entity.Entity;

public interface ConstantColorEntity extends GrayScaleEntity {

    default int getConstantColor() {
        return DataConstants.DEFAULT_NULL_COLOR_VALUE;
    }

    default boolean isColored() {
        return getConstantColor() != DataConstants.DEFAULT_NULL_COLOR_VALUE;
    }

    @Override
    default boolean isGrayScaled(Entity entity) {
        return GrayScaleEntity.super.isGrayScaled(entity);
    }

}
