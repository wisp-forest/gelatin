package io.wispforest.jello.misc;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class JelloPotions implements AutoRegistryContainer<Potion> {

    public static final Potion DRAGON_HEALTH = new Potion(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 3600));
    public static final Potion LONG_DRAGON_HEALTH = new Potion("dragon_health", new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 9600));
    public static final Potion STRONG_DRAGON_HEALTH = new Potion("dragon_health", new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 1800, 1));

    public static final Potion GOLDEN_LIQUID = new Potion(
            new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1),
            new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0)
    );

    public static final Potion ENCHANTED_GOLDEN_LIQUID = new Potion(
            new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1),
            new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0),
            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0),
            new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3)
    );

    public static final Potion NAUTICAL_POWER = new Potion(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 3600));
    public static final Potion LONG_NAUTICAL_POWER = new Potion("nautical_power", new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 9600));
    public static final Potion STRONG_NAUTICAL_POWER = new Potion("nautical_power", new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 1800, 1));

    public static final Potion VILLAGE_HERO = new Potion(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 900));

    public static final Potion DOLPHINS_PACT = new Potion(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 3600));
    public static final Potion LONG_DOLPHINS_PACT= new Potion("dolphins_pact", new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 9600));
    public static final Potion STRONG_DOLPHINS_PACT = new Potion("dolphins_pact", new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 1800, 1));

    @Override
    public Registry<Potion> getRegistry() {
        return Registries.POTION;
    }

    @Override
    public Class<Potion> getTargetFieldType() {
        return Potion.class;
    }
}
