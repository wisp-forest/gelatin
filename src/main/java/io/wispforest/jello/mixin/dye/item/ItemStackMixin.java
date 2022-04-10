package io.wispforest.jello.mixin.dye.item;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.ducks.DyeTool;
import io.wispforest.jello.api.ducks.entity.ConstantColorEntity;
import io.wispforest.jello.api.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.registry.ColorizeRegistry;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
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
        if(this.getItem() instanceof DyeTool dyeTool){
            if(!Jello.getConfig().enableDyeingEntities || (entity instanceof PlayerEntity && !Jello.getConfig().enableDyeingPlayers)){
                return;
            }

            if (ColorizeRegistry.isRegistered(entity)) {
                if (entity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()) {
                    return;
                }

                ActionResult result = dyeTool.attemptToDyeEntity(user.getWorld(), user, (DyeableEntity)entity, (ItemStack)(Object)this, hand);

                if (result != ActionResult.PASS) {
                    cir.setReturnValue(result);
                }
            }
        }
    }

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void jello$useDyeItemOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir, PlayerEntity playerEntity, BlockPos blockPos, CachedBlockPosition cachedBlockPosition, Item item){
        if(item instanceof DyeTool dyeTool){
            ActionResult result = dyeTool.attemptToDyeBlock(context);

            if(result != ActionResult.PASS){
                cir.setReturnValue(result);
            }
        }
    }
}
