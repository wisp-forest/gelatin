package io.wispforest.jello.mixins.client;

import io.wispforest.jello.item.dyebundle.DyeBundleScreenEvent;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryMixin {

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;init()V", shift = At.Shift.BY, by = 2))
    private void startEventRegistry(CallbackInfo ci) {
        ScreenMouseEvents.allowMouseScroll(((CreativeInventoryScreen) (Object) this)).register((screen, mouseX, mouseY, horizontalAmount, verticalAmount) -> new DyeBundleScreenEvent().allowMouseScroll(screen, mouseX, mouseY, horizontalAmount, verticalAmount));
    }
}
