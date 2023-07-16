package io.wispforest.gelatin.dye_entities.mixins.bugfix;

import io.wispforest.gelatin.common.CommonInit;
import io.wispforest.gelatin.dye_entities.ducks.DyeEntityTool;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HorseEntity.class)
public class HorseEntityMixin {

    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    private void fixInablityToDyeLlamaAndDonkey(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Item item = player.getStackInHand(hand).getItem();

        if ((item instanceof DyeEntityTool) && CommonInit.getConfig().isEntityDyeingEnabled()) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
