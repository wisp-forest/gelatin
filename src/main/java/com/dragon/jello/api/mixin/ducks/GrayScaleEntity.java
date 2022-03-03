package com.dragon.jello.api.mixin.ducks;

import com.dragon.jello.api.registry.GrayScaleRegistry;
import net.minecraft.entity.Entity;

public interface GrayScaleEntity {

    default boolean isGrayScaled(Entity entity){
        return GrayScaleRegistry.isRegistered(entity);
    }

    default boolean isTrueColored(){
        return true;
    }
}
