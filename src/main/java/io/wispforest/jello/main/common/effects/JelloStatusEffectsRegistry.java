package io.wispforest.jello.main.common.effects;

import io.wispforest.jello.main.mixin.mixins.StatusEffectAccessor;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.registry.Registry;

public class JelloStatusEffectsRegistry implements AutoRegistryContainer<StatusEffect> {

    public static final StatusEffect BOUNCE = StatusEffectAccessor.createStatusEffect(StatusEffectCategory.BENEFICIAL, 2293580);


    @Override
    public Registry<StatusEffect> getRegistry() {
        return Registry.STATUS_EFFECT;
    }

    @Override
    public Class<StatusEffect> getTargetFieldType() {
        return StatusEffect.class;
    }
}
