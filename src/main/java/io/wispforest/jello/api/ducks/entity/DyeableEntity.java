package io.wispforest.jello.api.ducks.entity;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.registry.GrayScaleRegistry;
import io.wispforest.jello.mixin.dye.item.EnchantedGoldenAppleMixin;
import io.wispforest.jello.mixin.entitycolor.LivingEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/**
 * An interface used within {@link LivingEntityMixin} to add Colorization to all of Minecrafts many {@link LivingEntity}
 */
public interface DyeableEntity extends ConstantColorEntity, RainbowEntity {

    /**
     * A method used to check if the Entity is Dyed or not based on its color ID.
     */
    default boolean isDyed() {
        return dyeColorOverride() || !getDyeColorID().equals(DyeColorantRegistry.NULL_VALUE_NEW.getId());
    }

    /**
     * A method used set your Entity's Color ID. The value of 16 is the default value, any higher will not affect the entity's color.
     */
    default Identifier getDyeColorID() {
        return DyeColorantRegistry.NULL_VALUE_NEW.getId();
    }

    /**
     * A method already implemented by {@link LivingEntityMixin} to change the Dye Color of entity based on {@link EnchantedGoldenAppleMixin} logic.
     */
    void setDyeColor(DyeColorant dyeColorID);

    /**
     * Returns the {@link net.minecraft.util.DyeColor} using the Entity's Dye Color ID
     */
    default DyeColorant getDyeColor() {
        return DyeColorantRegistry.DYE_COLOR.get(getDyeColorID());
    }

    /**
     * A method used to forever have A Certain DyeColor
     */
    boolean dyeColorOverride();

    @Override
    default boolean isGrayScaled(Entity entity) {
        return (isDyed() || isColored() || RainbowEntity.super.isGrayScaled(entity)) && !GrayScaleRegistry.isBlacklisted(entity);
    }

}
