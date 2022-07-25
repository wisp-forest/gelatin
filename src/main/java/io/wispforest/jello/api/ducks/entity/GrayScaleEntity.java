package io.wispforest.jello.api.ducks.entity;

import io.wispforest.jello.api.registry.GrayScaleEntityRegistry;
import net.minecraft.entity.Entity;

public interface GrayScaleEntity {

    default boolean isGrayScaled(Entity entity) {
        return GrayScaleEntityRegistry.isRegistered(entity) && !GrayScaleEntityRegistry.isBlacklisted(entity);
    }

    default boolean isTrueColored() {
        return true;
    }
}
