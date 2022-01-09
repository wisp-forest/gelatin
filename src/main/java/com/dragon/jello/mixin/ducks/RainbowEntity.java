package com.dragon.jello.mixin.ducks;

import com.dragon.jello.events.ColorEntityEvent;
import com.dragon.jello.mixin.mixins.LivingEntityMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

/**
 * RainbowEntity Interface is used to give your entity a Rainbow effect like the _Jeb easter Egg.
 * Only Implement this interface if you want to change how the Rainbow Effect is applyed
 * <br><br>
 * e.g. If you want your entity to default rainbow, change the {@link #isRainbowTime()} such that it only returns true
 */
public interface RainbowEntity {

    /**
     * A method used to tell if A entity will be rendered with the Rainbow Effect similar to _Jeb
     */
    default boolean isRainbowTime(){
       return rainbowOverride();
    };

    /**
     * Used by the {@link ColorEntityEvent} to disable rainbow event,
     *
     * @param value     boolean value for either turning on or off rainbow
     */
    void setRainbowTime(boolean value);

    /**
     * A method used to forever have Rainbow Effect Enabled
     */
    boolean rainbowOverride();

}
