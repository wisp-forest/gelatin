package io.wispforest.gelatin.common;

import io.wispforest.gelatin.common.compat.GelatinConfigHelper;
import io.wispforest.gelatin.common.compat.GelatinDefaultConfig;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.common.pas.ActionState;
import io.wispforest.gelatin.common.pas.impl.ServerPlayerActionStates;
import io.wispforest.gelatin.common.pas.PlayerActionStateHelper;
import io.wispforest.gelatin.common.util.TrackedDataHandlerExtended;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

public class CommonInit implements ModInitializer {

    @Override
    public void onInitialize() {
        ActionStateNetworking.init();

        ServerPlayConnectionEvents.DISCONNECT.register(ServerPlayerActionStates.INSTANCE);
        ServerPlayConnectionEvents.JOIN.register(ServerPlayerActionStates.INSTANCE);

        TrackedDataHandlerExtended.init();

        if(FabricLoader.getInstance().isModLoaded("gelatin-dye-entities") || FabricLoader.getInstance().isModLoaded("gelatin-dye-entries")){
            PlayerActionStateHelper.registerAction(GelatinConstants.DYE_TOGGLE_SYNC_ID, ActionState::new);
        }
    }

    public static GelatinConfigHelper getConfig() {
        if(GelatinConfigHelper.INSTANCE == null){
            GelatinConfigHelper.INSTANCE = GelatinDefaultConfig.INSTANCE;
        }

        return GelatinConfigHelper.INSTANCE;
    }
}
