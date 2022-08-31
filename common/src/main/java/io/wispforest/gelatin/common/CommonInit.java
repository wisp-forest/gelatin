package io.wispforest.gelatin.common;

import io.wispforest.gelatin.common.compat.GelatinConfig;
import io.wispforest.gelatin.common.util.TrackedDataHandlerExtended;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;

public class CommonInit implements ModInitializer {

    public static final GelatinConfig MAIN_CONFIG = GelatinConfig.createAndLoad();

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.processSimple(TrackedDataHandlerExtended.class, false);
    }

    public static GelatinConfig getConfig() {
        return MAIN_CONFIG;
    }
}
