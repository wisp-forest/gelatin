package com.dragon.jello.mixin.ducks;

import com.dragon.jello.events.ColorEntityEvent;
import com.dragon.jello.mixin.mixins.LivingEntityMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Unique;

public interface DyeableEntity extends ConstantColorEntity, RainbowEntity{

    /**
     * A method used set your Entity's Color ID. The value of 16 is the default value, any higher will not affect the entity's color.
     */
    default int getDyeColorID(){
        return 16;
    }

    /**
     * A method already implemented by {@link LivingEntityMixin} to change the Dye Color of entity based on {@link ColorEntityEvent} logic.
     */
    void setDyeColorID(int dyeColorID);

    /**
     * A method used to check if the Entity is Dyed or not based on its color ID.
     */
    default boolean isDyed(){
        return dyeColorOverride() || getDyeColorID() < 16;
    }

    /**
     * Returns the {@link DyeColor} using the Entity's Dye Color ID
     */
    default DyeColor getDyeColor(){
        return DyeColor.byId(getDyeColorID());
    }

    /**
     * A Override of {@link GrayScaleEntity#isGrayScaled()} such
     * that it only activates if the entity is {@link #isDyed()}.
     */
    default boolean isGrayScaled(){
        return trueColorOverride() && isDyed();
    }

    /**
     * A method used to forever have A Certain DyeColor
     */
    boolean dyeColorOverride();

}
