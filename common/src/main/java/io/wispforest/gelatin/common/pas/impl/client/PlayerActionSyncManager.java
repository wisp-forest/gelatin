package io.wispforest.gelatin.common.pas.impl.client;

import io.wispforest.gelatin.common.ActionStateNetworking;
import io.wispforest.gelatin.common.pas.ActionObservationType;
import io.wispforest.gelatin.common.util.VersatileLogger;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerActionSyncManager implements ClientTickEvents.StartTick {

    private static final VersatileLogger LOGGER = new VersatileLogger("PlayerActionSyncManager");

    public static PlayerActionSyncManager INSTANCE = new PlayerActionSyncManager();

    public Map<Identifier, ActionDataHandler> handlers = new HashMap<>();

    public static ActionDataHandler createHandler(Identifier syncID, KeyBinding binding, ActionObservationType type){
        return createHandler(syncID, binding, type, false);
    }

    public static ActionDataHandler createHandler(Identifier syncID, KeyBinding binding, ActionObservationType type, boolean defaultState){
        if(INSTANCE.handlers.containsKey(syncID)){
            LOGGER.failMessage("An existing Handler with the same SyncID was registered, meaning such won't be registered! [ID: {}]", syncID);

            return ActionDataHandler.EMPTY;
        }

        ActionDataHandler handler = new ActionDataHandler(syncID, binding, type);

        INSTANCE.handlers.put(syncID, handler);

        return handler;
    }

    @Override
    public void onStartTick(MinecraftClient client) {
        handlers.forEach((id, handler) -> {
            switch (handler.type){
                case PRESSED -> {
                    if(handler.binding.isPressed() == handler.keyState) return;

                    handler.keyState = handler.binding.isPressed();
                    handler.isDirty = true;
                }
                case TOGGLED -> {
                    if(!handler.binding.wasPressed()) return;

                    handler.keyState = !handler.keyState;
                    handler.isDirty = true;
                }
            }
        });

        Set<ActionDataHandler> dirtyHandlers = handlers.values().stream().filter(handler -> handler.isDirty).collect(Collectors.toSet());

        if(!dirtyHandlers.isEmpty()){
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeCollection(dirtyHandlers, (buf1, handler) -> handler.writeData(buf1));

            ClientPlayNetworking.send(ActionStateNetworking.ACTION_SYNC, buf);

            dirtyHandlers.forEach(handler -> handler.isDirty = false);
        }
    }

    public static class ActionDataHandler {
        private static final ActionDataHandler EMPTY = new ActionDataHandler(new Identifier("null", "na"), null, null);

        public final Identifier syncID;

        private KeyBinding binding;
        private ActionObservationType type;

        protected boolean keyState = false;
        private boolean isDirty = false;

        private ActionDataHandler(Identifier syncID, KeyBinding binding, ActionObservationType type){
            this.syncID = syncID;
            this.binding = binding;
            this.type = type;
        }

        public ActionDataHandler keyBinding(KeyBinding binding){
            this.binding = binding;

            return this;
        }

        public ActionDataHandler observationType(ActionObservationType type){
            this.type = type;

            return this;
        }

        public ActionObservationType observationType(){
            return this.type;
        }

        private void writeData(PacketByteBuf buf){
            buf.writeIdentifier(syncID);
            buf.writeBoolean(keyState);
        }
    }
}
