package com.dragon.jello.mixin.ducks;

import com.dragon.jello.events.ColorEntityEvent;
import com.dragon.jello.mixin.mixins.LivingEntityMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

public interface GrayScaleEntity {

    /**
     * Used by to disable or enable Gray Scale Mode on an Entity
     *
     * @param value     boolean value for either turning on or off rainbow
     */
    void setGrayScaleMode(boolean value);

    /**
     * Checks if the entity is currently Gray Scaled or not
     */
    default boolean isGrayScaled(){
        return grayScaleOverride();
    }

    /**
     * A method used to forever have Gray Scale Effect Enabled
     */
    boolean grayScaleOverride();
}
