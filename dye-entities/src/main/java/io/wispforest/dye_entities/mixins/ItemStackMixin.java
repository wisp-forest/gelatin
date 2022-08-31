package io.wispforest.dye_entities.mixins;

import io.wispforest.common.CommonInit;
import io.wispforest.dye_entities.ducks.ConstantColorEntity;
import io.wispforest.dye_entities.client.utils.ColorizeBlackListRegistry;
import io.wispforest.dye_registry.ducks.DyeEntityTool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(method = "useOnEntity", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void jello$useDyeItemOnEntity(PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if(this.getItem() instanceof DyeEntityTool dyeTool){
            if(!CommonInit.getConfig().enableDyeingEntities || (entity instanceof PlayerEntity && !CommonInit.getConfig().enableDyeingPlayers)){
                return;
            }

            if (!ColorizeBlackListRegistry.isBlackListed(entity)) {
                if (entity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()) {
                    return;
                }

                ActionResult result = dyeTool.attemptToDyeEntity(user.getWorld(), user, entity, (ItemStack)(Object)this, hand);

                if (result != ActionResult.PASS) {
                    cir.setReturnValue(result);
                }
            }
        }
    }
}