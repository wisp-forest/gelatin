package io.wispforest.gelatin.common.client;

import io.wispforest.gelatin.common.ActionStateNetworking;
import io.wispforest.gelatin.common.CommonInit;
import io.wispforest.gelatin.common.pas.ActionObservationType;
import io.wispforest.gelatin.common.pas.impl.client.ClientPlayerActionStates;
import io.wispforest.gelatin.common.pas.impl.client.PlayerActionSyncManager;
import io.wispforest.gelatin.common.client.util.WrappedKeyBinding;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.common.pas.PlayerActionStateHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class CommonClientInit implements ClientModInitializer {

    @Nullable private static KeyBinding DYE_COLORING_TOGGLE = null;
    @Nullable private static PlayerActionSyncManager.ActionDataHandler DYE_COLORING_HANDLER = null;

    @Override
    public void onInitializeClient() {
        ActionStateNetworking.clientInit();

        ClientPlayConnectionEvents.JOIN.register(ClientPlayerActionStates.INSTANCE);
        ClientTickEvents.START_CLIENT_TICK.register(PlayerActionSyncManager.INSTANCE);

        if(FabricLoader.getInstance().isModLoaded("gelatin-dye-entities") || FabricLoader.getInstance().isModLoaded("gelatin-dye-entries")){
            boolean separateBindings = CommonInit.getConfig().dyeingControls_useSeparateKeybinding();

            if(separateBindings){
                DYE_COLORING_TOGGLE = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.dye_coloring_toggle", GLFW.GLFW_KEY_V, GelatinConstants.GELATIN_KEY_CATEGORY));
            }

            Supplier<KeyBinding> binding = separateBindings
                    ? () -> DYE_COLORING_TOGGLE
                    : () -> MinecraftClient.getInstance().options.sneakKey;

            DYE_COLORING_HANDLER = PlayerActionSyncManager.createHandler(
                    GelatinConstants.DYE_TOGGLE_SYNC_ID,
                    new WrappedKeyBinding(binding).observationOnly(!separateBindings),
                    ActionObservationType.of(CommonInit.getConfig().dyeingControls_useToggleMode())
            );

            CommonInit.getConfig().dyeingControls_observeToggleMode(isToggleMode -> {
                DYE_COLORING_HANDLER.observationType(ActionObservationType.of(isToggleMode));
            });

            ClientPlayerActionStates.INSTANCE.modifyStateEvent(GelatinConstants.DYE_TOGGLE_SYNC_ID, state -> {
                state.onStateChangeEvent((player, newState) -> {
                    if(!player.getWorld().isClient() || !DYE_COLORING_HANDLER.observationType().isToggled()) return;

                    player.sendMessage(Text.of((newState ? "Enabled" : "Disabled") + " Dye Coloring"), true);
                }).setState(CommonInit.getConfig().dyeingControls_alwaysOnByDefault());
            });
        }
    }
}
