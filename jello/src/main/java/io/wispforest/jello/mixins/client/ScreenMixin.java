package io.wispforest.jello.mixins.client;

import io.wispforest.jello.api.HandledScreenEventFactory;
import io.wispforest.jello.api.HandledScreenEvents;
import io.wispforest.jello.api.HandledScreenExtension;
import io.wispforest.jello.api.OnItemstackTooltipRenderEvent;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin implements HandledScreenExtension {

    private Event<HandledScreenEvents.AllowSlotHover> JELLO$ALLOW_SLOT_HOVER;
    private Event<HandledScreenEvents.AllowMouseTooltipWithCursorStack> JELLO$ALLOW_MOUSE_TOOLTIP_WITH_CURSOR_STACK;

    private Event<HandledScreenEvents.AllowMouseDrag> JELLO$ALLOW_MOUSE_DRAG;
    private Event<HandledScreenEvents.BeforeMouseDrag> JELLO$BEFORE_MOUSE_DRAG;
    private Event<HandledScreenEvents.AfterMouseDrag> JELLO$AFTER_MOUSE_DRAG;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
    private void initHandledScreenEvents(MinecraftClient client, int width, int height, CallbackInfo ci){
        if((Object)(this) instanceof HandledScreen handledScreen){
            this.JELLO$ALLOW_SLOT_HOVER = HandledScreenEventFactory.createAllowSlotHoverEvent();
            this.JELLO$ALLOW_MOUSE_TOOLTIP_WITH_CURSOR_STACK = HandledScreenEventFactory.createAllowMouseTooltipWithCursorStack();

            this.JELLO$ALLOW_MOUSE_DRAG = HandledScreenEventFactory.createAllowMouseDragEvent();
            this.JELLO$BEFORE_MOUSE_DRAG = HandledScreenEventFactory.createBeforeMouseDragEvent();
            this.JELLO$AFTER_MOUSE_DRAG = HandledScreenEventFactory.createAfterMouseDragEvent();
        }
    }

    @Override
    public Event<HandledScreenEvents.AllowSlotHover> jello_getAllowSlotHoverEvent() {
        return ensureEventsAreInitialised(JELLO$ALLOW_SLOT_HOVER);
    }

    @Override
    public Event<HandledScreenEvents.AllowMouseTooltipWithCursorStack> jello_getAllowMouseTooltipWithCursorStack() {
        return ensureEventsAreInitialised(JELLO$ALLOW_MOUSE_TOOLTIP_WITH_CURSOR_STACK);
    }

    @Override
    public Event<HandledScreenEvents.AllowMouseDrag> jello_getAllowMouseDragEvent() {
        return ensureEventsAreInitialised(JELLO$ALLOW_MOUSE_DRAG);
    }

    @Override
    public Event<HandledScreenEvents.BeforeMouseDrag> jello_getBeforeMouseDragEvent() {
        return ensureEventsAreInitialised(JELLO$BEFORE_MOUSE_DRAG);
    }

    @Override
    public Event<HandledScreenEvents.AfterMouseDrag> jello_getAfterMouseDragEvent() {
        return ensureEventsAreInitialised(JELLO$AFTER_MOUSE_DRAG);
    }

    @Unique
    private <T> Event<T> ensureEventsAreInitialised(Event<T> event) {
        if (event == null) {
            throw new IllegalStateException(String.format("[fabric-screen-api-v1] The current screen (%s) has not been correctly initialised, please send this crash log to the mod author. This is usually caused by calling setScreen on the wrong thread.", this.getClass().getName()));
        }

        return event;
    }
}
