package io.wispforest.gelatin.common.misc;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class GelatinConstants {
    public static final String MODID = "gelatin";

    public static final boolean DEBUG_ENV_VAR = Boolean.getBoolean("gelatin.debug");
    public static final boolean DEBUG_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();

    public static final String GELATIN_KEY_CATEGORY = "key.categories.gelatin";

    public static final Identifier DYE_TOGGLE_SYNC_ID = GelatinConstants.id("dye_toggle");

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }
}