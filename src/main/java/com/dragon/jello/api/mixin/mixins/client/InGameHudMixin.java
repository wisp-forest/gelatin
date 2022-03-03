package com.dragon.jello.api.mixin.mixins.client;

import com.dragon.jello.api.dye.item.DyenamicDyeItem;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow private int heldItemTooltipFade;

    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getMainHandStack()Lnet/minecraft/item/ItemStack;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void displayTextForever(CallbackInfo ci, Entity entity, ItemStack itemStack){
        if(itemStack.getItem() instanceof DyenamicDyeItem){
            this.heldItemTooltipFade = this.heldItemTooltipFade + 1;
        }
    }
}
