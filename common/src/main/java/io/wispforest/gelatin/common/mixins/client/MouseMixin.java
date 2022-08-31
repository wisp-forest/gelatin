package io.wispforest.gelatin.common.mixins.client;

import io.wispforest.gelatin.common.events.HotbarMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow @Final private MinecraftClient client;

    @Unique private Double horizontalScrollAmount;

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void beforePlayerScrollHotbar(long window, double horizontal, double vertical, CallbackInfo ci, double verticalAmount, int i) {

        this.horizontalScrollAmount = this.client.options.getDiscreteMouseScroll().getValue() ? Math.signum(horizontal) : horizontal * this.client.options.getMouseWheelSensitivity().getValue();

        if (!HotbarMouseEvents.ALLOW_MOUSE_SCROLL.invoker().allowMouseScroll(this.client.player, horizontalScrollAmount, verticalAmount)) {
            this.horizontalScrollAmount = null;

            ci.cancel();
            return;
        }

        HotbarMouseEvents.BEFORE_MOUSE_SCROLL.invoker().beforeMouseScroll(this.client.player, horizontalScrollAmount, verticalAmount);
    }

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void afterPlayerScrollHotbar(long window, double horizontal, double vertical, CallbackInfo ci, double verticalAmount, int i) {
        HotbarMouseEvents.AFTER_MOUSE_SCROLL.invoker().afterMouseScroll(this.client.player, horizontalScrollAmount, verticalAmount);

        this.horizontalScrollAmount = null;
    }
}
