package io.wispforest.jello.misc;

import io.wispforest.jello.mixin.StatusEffectAccessor;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.registry.Registry;

public class JelloStatusEffectsRegistry implements AutoRegistryContainer<StatusEffect> {

    public static final StatusEffect BOUNCE = StatusEffectAccessor.jello$invokeNew(StatusEffectCategory.BENEFICIAL, 2293580);


    @Override
    public Registry<StatusEffect> getRegistry() {
        return Registry.STATUS_EFFECT;
    }

    @Override
    public Class<StatusEffect> getTargetFieldType() {
        return StatusEffect.class;
    }
}
