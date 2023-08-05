package io.wispforest.gelatin.common.pas.impl.client;

import io.wispforest.gelatin.common.ActionStateNetworking;
import io.wispforest.gelatin.common.pas.ActionState;
import io.wispforest.gelatin.common.pas.PlayerActionStateHelper;
import io.wispforest.gelatin.common.pas.PlayerActionStatesStorage;
import io.wispforest.gelatin.common.util.VersatileLogger;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ClientPlayerActionStates implements PlayerActionStatesStorage, ClientPlayConnectionEvents.Join, ClientPlayNetworking.PlayChannelHandler {

    private static final VersatileLogger LOGGER = new VersatileLogger("ClientPlayerActionStatesStorage");

    public static final ClientPlayerActionStates INSTANCE = new ClientPlayerActionStates();

    //-------

    private final Map<Identifier, Consumer<ActionState>> stateModifyEvents = new HashMap<>();

    private final Map<Identifier, ActionState> stateStorage = new ConcurrentHashMap<>();

    private ClientPlayerActionStates(){}

    //-------

    public void modifyStateEvent(Identifier syncID, Consumer<ActionState> consumer){
        if(stateModifyEvents.containsKey(syncID)){
            LOGGER.failMessage("It seems that an ModifyEvent already has been registered, meaning such will be ignored! [ID: {}]", syncID);
        }

        stateModifyEvents.put(syncID, consumer);
    }

    @Override
    public Map<Identifier, ActionState> getPlayersStateStorage(UUID pUUID) {
        return stateStorage;
    }

    @Override
    public boolean dosePlayerHaveStorage(UUID pUUID) {
        return MinecraftClient.getInstance().player.getUuid().equals(pUUID);
    }

    //-------

    @Override
    public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        stateStorage.clear();

        Map<Identifier, Boolean> defaultValues = new HashMap<>();

        PlayerActionStateHelper.getRegisteredFactories().forEach((syncID, factory) -> {
            ActionState state = factory.createState();

            if(stateModifyEvents.containsKey(syncID)) stateModifyEvents.get(syncID).accept(state);

            defaultValues.put(syncID, state.get());

            stateStorage.put(syncID, state);
        });

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeCollection(defaultValues.entrySet(), (buf1, entry) -> {
            buf1.writeIdentifier(entry.getKey());
            buf1.writeBoolean(entry.getValue());
        });

        ClientPlayNetworking.send(ActionStateNetworking.ACTION_DEFAULT_SYNC, buf);

        PlayerActionSyncManager.INSTANCE.handlers.forEach((syncID, aHandler) -> {
            if(!defaultValues.containsKey(syncID)){
                LOGGER.failMessage("Mismatch between Storage and PlayerActionSyncManager which means things may not work and default value is not set! [ID: {}]", syncID);

                return;
            }

            aHandler.keyState = defaultValues.get(syncID);
        });
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int collectionSize = buf.readVarInt();

        Map<Identifier, Boolean> changedData = new HashMap<>();

        for(int i = 0; i < collectionSize; i++){
            Identifier syncID = buf.readIdentifier();
            boolean state = buf.readBoolean();

            if (!stateStorage.containsKey(syncID)) {
                LOGGER.warnMessage("A ActionState update was sent but can not be found on the server, such will be ignored! [ID: {}]", syncID);

                continue;
            }

            ActionState currentState = stateStorage.get(syncID);

            if (state == currentState.get()) {
                LOGGER.warnMessage("A ActionState update was sent but nothing changed between the client and the server, such will be ignored! [ID: {}]", syncID);

                continue;
            }

            changedData.put(syncID, state);

            currentState.setState(state);
        }

        client.execute(() -> changedData.keySet().forEach((syncID) -> stateStorage.get(syncID).onChange(client.player)));
    }
}
