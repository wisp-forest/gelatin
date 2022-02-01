package com.dragon.jello.mixin.ducks;

import com.dragon.jello.registry.GrayScaleRegistry;
import net.minecraft.entity.Entity;

public interface GrayScaleEntity {

    default boolean isGrayScaled(Entity entity){
        return GrayScaleRegistry.isRegistered(entity);
    }

    default boolean isTrueColored(){
        return true;
    }
}
