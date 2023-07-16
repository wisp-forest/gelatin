package io.wispforest.gelatin.common;

import io.wispforest.gelatin.common.compat.GelatinConfigHelper;
import io.wispforest.gelatin.common.compat.GelatinDefaultConfig;
import io.wispforest.gelatin.common.util.TrackedDataHandlerExtended;
import net.fabricmc.api.ModInitializer;

public class CommonInit implements ModInitializer {

    @Override
    public void onInitialize() {
        TrackedDataHandlerExtended.init();
    }

    public static GelatinConfigHelper getConfig() {
        if(GelatinConfigHelper.INSTANCE == null){
            GelatinConfigHelper.INSTANCE = GelatinDefaultConfig.INSTANCE;
        }

        return GelatinConfigHelper.INSTANCE;
    }
}
