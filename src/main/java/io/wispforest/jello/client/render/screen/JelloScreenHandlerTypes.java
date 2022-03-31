package io.wispforest.jello.client.render.screen;

import io.wispforest.jello.Jello;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class JelloScreenHandlerTypes {

    public static final ScreenHandlerType<ColorMixerScreenHandler> COLOR_MIXER_TYPE = ScreenHandlerRegistry.
            registerSimple(Jello.id("color_mixer"), ColorMixerScreenHandler::new);

    public static void initialize() {}
}
