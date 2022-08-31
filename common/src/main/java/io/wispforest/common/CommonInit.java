package io.wispforest.common;

import io.wispforest.common.compat.GelatinConfig;
import io.wispforest.common.util.TrackedDataHandlerExtended;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class CommonInit implements ModInitializer {

    public static ConfigHolder<GelatinConfig> MAIN_CONFIG = null;

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.processSimple(TrackedDataHandlerExtended.class, false);
    }

    public static GelatinConfig getConfig() {
        if(MAIN_CONFIG == null){
            AutoConfig.register(GelatinConfig.class, GsonConfigSerializer::new);

            MAIN_CONFIG = AutoConfig.getConfigHolder(GelatinConfig.class);
        }

        return MAIN_CONFIG.getConfig();
    }
}
