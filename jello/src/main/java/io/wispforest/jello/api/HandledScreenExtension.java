package io.wispforest.jello.api;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public interface HandledScreenExtension {

    static HandledScreenExtension getExtensions(HandledScreen<?> screen) {
        return (HandledScreenExtension) screen;
    }

    Event<HandledScreenEvents.AllowSlotHover> jello_getAllowSlotHoverEvent();
    Event<HandledScreenEvents.AllowMouseTooltipWithCursorStack> jello_getAllowMouseTooltipWithCursorStack();


    Event<HandledScreenEvents.AllowMouseDrag> jello_getAllowMouseDragEvent();
    Event<HandledScreenEvents.BeforeMouseDrag> jello_getBeforeMouseDragEvent();
    Event<HandledScreenEvents.AfterMouseDrag> jello_getAfterMouseDragEvent();
}
