package io.wispforest.gelatin;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class Gelatin implements PreLaunchEntrypoint, ModInitializer {

    @Override
    public void onPreLaunch() {
        if(FabricLoader.getInstance().isModLoaded("owo")) GelatinOwoConfigHelper.init();
    }

    @Override
    public void onInitialize() {
        if(FabricLoader.getInstance().isModLoaded("owo")) GelatinOwoConfigHelper.getConfig();
    }
}
