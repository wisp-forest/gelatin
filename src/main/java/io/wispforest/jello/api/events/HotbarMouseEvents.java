package io.wispforest.jello.api.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.Objects;

public class HotbarMouseEvents {

    /**
     * An event that is checks if the mouse should be allowed for the scroll bar.
     *
     * <p>This event tracks amount a mouse was scrolled vertically but not horizontally.
     *
     * @return the event
     */
    public static Event<AllowMouseScroll> ALLOW_MOUSE_SCROLL = EventFactory.createArrayBacked(HotbarMouseEvents.AllowMouseScroll.class,
            (listeners) -> (player, verticalAmount, horizontalAmount) -> {
                for (AllowMouseScroll event : listeners) {
                    if(!event.allowMouseScroll(player, verticalAmount, horizontalAmount)){
                        return false;
                    }
                }

                return true;
            }
    );

    /**
     * An event that is called before mouse scrolling is processed for the scroll bar.
     *
     * <p>This event tracks amount a mouse was scrolled vertically but not horizontally.
     *
     * @return the event
     */
    public static Event<BeforeMouseScroll> BEFORE_MOUSE_SCROLL = EventFactory.createArrayBacked(HotbarMouseEvents.BeforeMouseScroll.class,
            (listeners) -> (player, verticalAmount, horizontalAmount) -> {
                for (BeforeMouseScroll event : listeners) {
                    event.beforeMouseScroll(player, verticalAmount, horizontalAmount);
                }
            }
    );

    /**
     * An event that is called after mouse scrolling is processed for the scroll bar.
     *
     * <p>This event tracks amount a mouse was scrolled vertically but not horizontally.
     *
     * @return the event
     */
    public static Event<AfterMouseScroll> AFTER_MOUSE_SCROLL = EventFactory.createArrayBacked(HotbarMouseEvents.AfterMouseScroll.class,
            (listeners) -> (player, verticalAmount, horizontalAmount) -> {
                for (AfterMouseScroll event : listeners) {
                    event.afterMouseScroll(player, verticalAmount, horizontalAmount);
                }
            }
    );

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface AllowMouseScroll {
        /**
         * Checks if the mouse should be allowed to scroll in the hotbar.
         *
         * @param player the player gathered from the client who this event is being called from
         * @param horizontalAmount the horizontal scroll amount
         * @param verticalAmount the vertical scroll amount
         * @return whether the mouse should be allowed to scroll
         */
        boolean allowMouseScroll(ClientPlayerEntity player, double horizontalAmount, double verticalAmount);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface BeforeMouseScroll {
        /**
         * Called before a mouse has scrolled on screen.
         *
         * @param player the player gathered from the client who this event is being called from
         * @param horizontalAmount the horizontal scroll amount
         * @param verticalAmount the vertical scroll amount
         */
        void beforeMouseScroll(ClientPlayerEntity player, double horizontalAmount, double verticalAmount);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface AfterMouseScroll {
        /**
         * Called after a mouse has scrolled on screen.
         *
         * @param player the player gathered from the client who this event is being called from
         * @param horizontalAmount the horizontal scroll amount
         * @param verticalAmount the vertical scroll amount
         */
        void afterMouseScroll(ClientPlayerEntity player, double horizontalAmount, double verticalAmount);
    }
}
