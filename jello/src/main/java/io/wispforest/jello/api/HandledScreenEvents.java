package io.wispforest.jello.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Objects;

public class HandledScreenEvents {

    public static Event<AllowSlotHover> allowSlotHover(HandledScreen<?> screen){
        Objects.requireNonNull(screen, "Screen cannot be null");

        return HandledScreenExtension.getExtensions(screen).jello_getAllowSlotHoverEvent();
    }

    public static Event<AllowMouseTooltipWithCursorStack> allowMouseTooltipWithCursorStack(HandledScreen<?> screen){
        Objects.requireNonNull(screen, "Screen cannot be null");
        return HandledScreenExtension.getExtensions(screen).jello_getAllowMouseTooltipWithCursorStack();
    }

    /**
     * An event that checks if the mouse drag should be allowed.
     *
     * @return the event
     */
    public static Event<AllowMouseDrag> allowMouseDrag(HandledScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");

        return HandledScreenExtension.getExtensions(screen).jello_getAllowMouseDragEvent();
    }

    /**
     * An event that is called before a mouse drag is processed for a screen.
     *
     * @return the event
     */
    public static Event<BeforeMouseDrag> beforeMouseDrag(HandledScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");

        return HandledScreenExtension.getExtensions(screen).jello_getBeforeMouseDragEvent();
    }

    /**
     * An event that is called after a mouse drag is processed for a screen.
     *
     * @return the event
     */
    public static Event<AfterMouseDrag> afterMouseDrag(HandledScreen<?> screen) {
        Objects.requireNonNull(screen, "Screen cannot be null");

        return HandledScreenExtension.getExtensions(screen).jello_getAfterMouseDragEvent();
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface AllowMouseDrag {
        /**
         * @param mouseX the x position of the mouse
         * @param mouseY the y position of the mouse
         * @param button the button number, which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}.
         * @param deltaX the total x distance for the drag
         * @param deltaY the total y distance for the drag
         * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
         */
        boolean allowMouseDrag(HandledScreen<?> screen, double mouseX, double mouseY, int button, double deltaX, double deltaY);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface BeforeMouseDrag {
        /**
         * @param mouseX the x position of the mouse
         * @param mouseY the y position of the mouse
         * @param button the button number, which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}.
         * @param deltaX the total x distance for the drag
         * @param deltaY the total y distance for the drag
         * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
         */
        void beforeMouseDrag(HandledScreen<?> screen, double mouseX, double mouseY, int button, double deltaX, double deltaY);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface AfterMouseDrag {
        /**
         * @param mouseX the x position of the mouse
         * @param mouseY the y position of the mouse
         * @param button the button number, which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}.
         * @param deltaX the total x distance for the drag
         * @param deltaY the total y distance for the drag
         * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
         */
        void afterMouseDrag(HandledScreen<?> screen, double mouseX, double mouseY, int button, double deltaX, double deltaY);
    }

    public interface AllowSlotHover {
        boolean allowSlotHover(HandledScreen<?> screen, Slot slot, double pointX, double pointY);
    }

    public interface AllowMouseTooltipWithCursorStack {
        boolean allowMouseTooltipWithCursorStack(HandledScreen<?> screen, ItemStack cursorStack, Slot slot, double pointX, double pointY);
    }

}
