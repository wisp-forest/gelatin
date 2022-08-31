package io.wispforest.dye_entities.mixins.bugfix;

import io.wispforest.common.CommonInit;
import io.wispforest.dye_registry.ducks.DyeBlockTool;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractDonkeyEntity.class)
public class AbstractDonkeyEntityMixin {

    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    private void fixInablityToDyeLlamaAndDonkey(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Item item = player.getStackInHand(hand).getItem();

        if ((item instanceof DyeBlockTool) && CommonInit.getConfig().enableDyeingEntities) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
