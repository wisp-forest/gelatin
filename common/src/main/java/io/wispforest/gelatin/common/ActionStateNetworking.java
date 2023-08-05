package io.wispforest.gelatin.common;

import io.wispforest.gelatin.common.pas.impl.client.ClientPlayerActionStates;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.common.pas.impl.ServerPlayerActionStates;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ActionStateNetworking {

    public static final Identifier ACTION_SYNC = GelatinConstants.id("action_sync");
    public static final Identifier ACTION_DEFAULT_SYNC = GelatinConstants.id("action_default_sync");

    public static void init(){
        ServerPlayNetworking.registerGlobalReceiver(ACTION_SYNC, ServerPlayerActionStates.INSTANCE::receiveStateChange);
        ServerPlayNetworking.registerGlobalReceiver(ACTION_DEFAULT_SYNC, ServerPlayerActionStates.INSTANCE::receivePlayerDefaults);
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit(){
        ClientPlayNetworking.registerGlobalReceiver(ActionStateNetworking.ACTION_SYNC, ClientPlayerActionStates.INSTANCE);
    }
}
