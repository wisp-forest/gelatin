package io.wispforest.gelatin.common.pas.impl;

import io.wispforest.gelatin.common.ActionStateNetworking;
import io.wispforest.gelatin.common.pas.ActionState;
import io.wispforest.gelatin.common.pas.PlayerActionStateHelper;
import io.wispforest.gelatin.common.pas.PlayerActionStatesStorage;
import io.wispforest.gelatin.common.util.VersatileLogger;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerPlayerActionStates implements PlayerActionStatesStorage, ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {

    private static final VersatileLogger LOGGER = new VersatileLogger("ServerPlayerActionStatesStorage");

    public static ServerPlayerActionStates INSTANCE = new ServerPlayerActionStates();

    //-------

    private final Map<Identifier, Consumer<ActionState>> stateModifyEvents = new HashMap<>();

    private final Map<UUID, Map<Identifier, ActionState>> playerUUIDtoState = new ConcurrentHashMap<>();

    private ServerPlayerActionStates(){}

    //-----

    public void modifyStateEvent(Identifier syncID, Consumer<ActionState> consumer){
        if(stateModifyEvents.containsKey(syncID)){
            LOGGER.failMessage("It seems that an ModifyEvent already has been registered, meaning such will be ignored! [ID: {}]", syncID);
        }

        stateModifyEvents.put(syncID, consumer);
    }

    @Override
    public Map<Identifier, ActionState> getPlayersStateStorage(UUID pUUID) {
        return playerUUIDtoState.get(pUUID);
    }

    @Override
    public boolean dosePlayerHaveStorage(UUID pUUID) {
        return playerUUIDtoState.containsKey(pUUID);
    }

    //-----

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        UUID pUUID = handler.player.getUuid();

        Map<Identifier, ActionState> stateData = new ConcurrentHashMap<>();

        PlayerActionStateHelper.getRegisteredFactories().forEach((syncID, factory) -> {
            ActionState state = factory.createState();

            if(stateModifyEvents.containsKey(syncID)) stateModifyEvents.get(syncID).accept(state);

            stateData.put(syncID, state);
        });

        playerUUIDtoState.put(pUUID, stateData);
    }

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        playerUUIDtoState.remove(handler.player.getUuid());
    }

    public void receivePlayerDefaults(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID pUUID = player.getUuid();

        if(!playerUUIDtoState.containsKey(pUUID)) {
            LOGGER.failMessage("Unable to locate State Storage for player, defaults will not be updated! [UUID:{}, Name: {}]", pUUID, player.getName());

            return;
        }

        Map<Identifier, ActionState> playerKeystateData = playerUUIDtoState.get(pUUID);

        int totalPairs = buf.readVarInt();

        for (int i = 0; i < totalPairs; i++) {
            Identifier syncID = buf.readIdentifier();
            boolean state = buf.readBoolean();

            if (!playerKeystateData.containsKey(syncID)) {
                LOGGER.warnMessage("A ActionState default was sent but can not be found on the server, such will be ignored! [ID: {}]", syncID);

                continue;
            }

            playerKeystateData.get(syncID).setState(state);
        }
    }

    public void receiveStateChange(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID pUUID = player.getUuid();

        if(!playerUUIDtoState.containsKey(pUUID)) {
            LOGGER.failMessage("Unable to locate State Storage for player, data will not be updated! [UUID:{}, Name: {}]", pUUID, player.getName());

            return;
        }

        Map<Identifier, ActionState> playerKeystateData = playerUUIDtoState.get(pUUID);

        int totalPairs = buf.readVarInt();

        Map<Identifier, Boolean> changeData = new HashMap<>();

        for (int i = 0; i < totalPairs; i++) {
            Identifier syncID = buf.readIdentifier();
            boolean state = buf.readBoolean();

            if (!playerKeystateData.containsKey(syncID)) {
                LOGGER.warnMessage("A ActionState update was sent but can not be found on the server, such will be ignored! [ID: {}]", syncID);

                continue;
            }

            ActionState currentState = playerKeystateData.get(syncID);

            if (state == currentState.get()) {
                LOGGER.warnMessage("A ActionState update was sent but nothing changed between the client and the server, such will be ignored! [ID: {}]", syncID);

                continue;
            }

            changeData.put(syncID, state);

            currentState.setState(state);
        }

        server.execute(() -> {
            changeData.keySet().forEach((syncID) -> playerKeystateData.get(syncID).onChange(player));

            PacketByteBuf buf1 = PacketByteBufs.create();

            buf1.writeCollection(changeData.entrySet(), (buf2, entry) -> {
                buf2.writeIdentifier(entry.getKey());
                buf2.writeBoolean(entry.getValue());
            });

            ServerPlayNetworking.send(player, ActionStateNetworking.ACTION_SYNC, buf1);
        });
    }

    //-----


}
