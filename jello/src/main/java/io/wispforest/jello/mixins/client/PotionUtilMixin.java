package io.wispforest.jello.mixins.client;

import io.wispforest.jello.misc.JelloPotions;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionUtil.class)
public class PotionUtilMixin {

    @Inject(method = "getColor(Lnet/minecraft/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private static void jello$changeColor1(ItemStack stack, CallbackInfoReturnable<Integer> cir){
        if(stack.hasNbt() && stack.getNbt().contains("CustomPotionColor")) return;

        Potion potion = PotionUtil.getPotion(stack);

        if(potion == JelloPotions.GOLDEN_LIQUID || potion == JelloPotions.ENCHANTED_GOLDEN_LIQUID){
            cir.setReturnValue(Color.ofDye(DyeColor.YELLOW).rgb());
        }
    }

    @Inject(method = "getColor(Lnet/minecraft/potion/Potion;)I", at = @At("HEAD"), cancellable = true)
    private static void jello$changeColor2(Potion potion, CallbackInfoReturnable<Integer> cir){
        if(potion == JelloPotions.GOLDEN_LIQUID || potion == JelloPotions.ENCHANTED_GOLDEN_LIQUID){
            cir.setReturnValue(Color.ofDye(DyeColor.YELLOW).rgb());
        }
    }
}
