package io.wispforest.jello.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class HandledScreenEventFactory {

    public static Event<HandledScreenEvents.AllowSlotHover> createAllowSlotHoverEvent() {
        return EventFactory.createArrayBacked(HandledScreenEvents.AllowSlotHover.class, callbacks -> (screen, slot, pointX, pointY) -> {
            for(HandledScreenEvents.AllowSlotHover callback : callbacks){
                if(!callback.allowSlotHover(screen, slot, pointX, pointY)) return false;
            }

            return true;
        });
    }

    public static Event<HandledScreenEvents.AllowMouseTooltipWithCursorStack> createAllowMouseTooltipWithCursorStack() {
        return EventFactory.createArrayBacked(HandledScreenEvents.AllowMouseTooltipWithCursorStack.class, callbacks -> (screen, cursorStack, slot, pointX, pointY) -> {
            for(HandledScreenEvents.AllowMouseTooltipWithCursorStack callback : callbacks){
                if(!callback.allowMouseTooltipWithCursorStack(screen, cursorStack, slot, pointX, pointY)) return false;
            }

            return true;
        });
    }

    public static Event<HandledScreenEvents.AllowMouseDrag> createAllowMouseDragEvent() {
        return EventFactory.createArrayBacked(HandledScreenEvents.AllowMouseDrag.class, callbacks -> (screen, mouseX, mouseY, button, deltaX, deltaY) -> {
            for(HandledScreenEvents.AllowMouseDrag callback : callbacks){
                if(!callback.allowMouseDrag(screen, mouseX, mouseY, button, deltaX, deltaY)) return false;
            }

            return true;
        });
    }

    public static Event<HandledScreenEvents.BeforeMouseDrag> createBeforeMouseDragEvent() {
        return EventFactory.createArrayBacked(HandledScreenEvents.BeforeMouseDrag.class, callbacks -> (screen, mouseX, mouseY, button, deltaX, deltaY) -> {
            for(HandledScreenEvents.BeforeMouseDrag callback : callbacks){
                callback.beforeMouseDrag(screen, mouseX, mouseY, button, deltaX, deltaY);
            }
        });
    }

    public static Event<HandledScreenEvents.AfterMouseDrag> createAfterMouseDragEvent() {
        return EventFactory.createArrayBacked(HandledScreenEvents.AfterMouseDrag.class, callbacks -> (screen, mouseX, mouseY, button, deltaX, deltaY) -> {
            for(HandledScreenEvents.AfterMouseDrag callback : callbacks){
                callback.afterMouseDrag(screen, mouseX, mouseY, button, deltaX, deltaY);
            }
        });
    }
}
