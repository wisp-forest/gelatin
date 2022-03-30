package io.wispforest.jello.main.client.render.screen;

import io.wispforest.jello.main.common.Jello;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class JelloScreenHandlerRegistry {

    public static final ScreenHandlerType<ColorMixerScreenHandler> COLOR_MIXER_TYPE = ScreenHandlerRegistry.
            registerSimple(new Identifier(Jello.MODID, "color_mixer"), ColorMixerScreenHandler::new);

    public static void init() {

    }
}
