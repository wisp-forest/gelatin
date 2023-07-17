package io.wispforest.jello.mixins.client;

import io.wispforest.jello.api.HandledScreenEvents;
import io.wispforest.jello.api.HandledScreenExtension;
import io.wispforest.jello.api.OnItemstackTooltipRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements HandledScreenExtension {

    @Shadow @Final protected T handler;
    @Shadow @Nullable protected Slot focusedSlot;

    @Shadow protected abstract List<Text> getTooltipFromItem(ItemStack stack);

    private boolean gelatin$drewMouseoverTooltip = false;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "isPointOverSlot", at = @At("HEAD"), cancellable = true)
    private void gelatin$isPointOverSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir){
        if(!HandledScreenEvents.allowSlotHover(handledScreen()).invoker().allowSlotHover(handledScreen(), slot, pointX, pointY)){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void gelatin$beforeMouseDragEvent(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir){
        if(!HandledScreenEvents.allowMouseDrag(handledScreen()).invoker().allowMouseDrag(handledScreen(), mouseX, mouseY, button, deltaX, deltaY)){
            cir.setReturnValue(false);
        }

        HandledScreenEvents.beforeMouseDrag(handledScreen()).invoker().beforeMouseDrag(handledScreen(), mouseX, mouseY, button, deltaX, deltaY);
    }

    @Inject(method = "mouseDragged", at = @At("RETURN"), cancellable = true)
    private void gelatin$afterMouseDragEvent(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir){
        HandledScreenEvents.afterMouseDrag(handledScreen()).invoker().afterMouseDrag(handledScreen(), mouseX, mouseY, button, deltaX, deltaY);
    }

    @Inject(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void gelatin$hasDrawnMouseoverTooltip(DrawContext context, int x, int y, CallbackInfo ci){
        this.gelatin$drewMouseoverTooltip = true;
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("TAIL"))
    private void gelatin$allowMouseTooltipWithCursorStack(DrawContext context, int x, int y, CallbackInfo ci){
        if (!gelatin$drewMouseoverTooltip){
            ItemStack cursorStack = this.handler.getCursorStack();

            if(!cursorStack.isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
                if(HandledScreenEvents.allowMouseTooltipWithCursorStack(handledScreen()).invoker()
                        .allowMouseTooltipWithCursorStack(handledScreen(), this.handler.getCursorStack(), focusedSlot, x, y)){
                    context.drawTooltip(MinecraftClient.getInstance().textRenderer,  this.getTooltipFromItem(this.focusedSlot.getStack()), x, y);
                }
            }
        }

        this.gelatin$drewMouseoverTooltip = false;
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void jello$OnItemstackTooltipRenderEvent(DrawContext context, int x, int y, CallbackInfo ci){
        if(this.focusedSlot == null || !this.focusedSlot.hasStack()) return;

        if(!OnItemstackTooltipRenderEvent.PRE_TOOLTIP_RENDER.invoker().onRender((Screen) (Object) this, context, this.focusedSlot.getStack(), x, y)) ci.cancel();
    }

    private HandledScreen<?> handledScreen(){
        return (HandledScreen<?>) (Object) this;
    }
}
