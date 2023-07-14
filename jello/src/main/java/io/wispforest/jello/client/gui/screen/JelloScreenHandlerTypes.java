package io.wispforest.jello.client.gui.screen;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class JelloScreenHandlerTypes implements AutoRegistryContainer<ScreenHandlerType<?>> {

    public static final ScreenHandlerType<ColorMixerScreenHandler> COLOR_MIXER_TYPE = new ScreenHandlerType<>(ColorMixerScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES); //register(Jello.interactionId("color_mixer"), ColorMixerScreenHandler::new)

    @Override
    public Registry<ScreenHandlerType<?>> getRegistry() {
        return Registries.SCREEN_HANDLER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ScreenHandlerType<?>> getTargetFieldType() {
        return (Class<ScreenHandlerType<?>>) (Object) ScreenHandlerType.class;
    }
}
