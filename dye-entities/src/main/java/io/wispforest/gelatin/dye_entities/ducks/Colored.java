package io.wispforest.gelatin.dye_entities.ducks;

import io.wispforest.gelatin.dye_entities.client.utils.GrayScaleEntityRegistry;
import net.minecraft.entity.Entity;

public interface Colored {

    int getColor(float delta);

    boolean isColored();

    boolean isRainbow();

    default boolean isGrayScaled(Entity entity) {
        return isGrayScaled(entity, RenderType.ENTITY_RENDER);
    }

    default boolean isGrayScaled(Entity entity, RenderType type) {
        return !GrayScaleEntityRegistry.isBlacklisted(entity);
    }

    public enum RenderType {
        FEATURE_RENDER,
        ENTITY_RENDER
    }
}
