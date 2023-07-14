package io.wispforest.gelatin.dye_entities.ducks;

import io.wispforest.gelatin.dye_entities.client.utils.GrayScaleEntityRegistry;
import net.minecraft.entity.Entity;

public interface GrayScaleEntity {

    default boolean isGrayScaled(Entity entity) {
        return GrayScaleEntityRegistry.isRegistered(entity) && !GrayScaleEntityRegistry.isBlacklisted(entity);
    }

    default boolean isTrueColored() {
        return true;
    }
}