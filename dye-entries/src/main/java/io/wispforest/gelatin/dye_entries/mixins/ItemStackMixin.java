package io.wispforest.gelatin.dye_entries.mixins;

import io.wispforest.gelatin.dye_entries.ducks.DyeBlockTool;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void jello$useDyeItemOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir, PlayerEntity playerEntity, BlockPos blockPos, CachedBlockPosition cachedBlockPosition, Item item){
        if(item instanceof DyeBlockTool dyeTool){
            ActionResult result = dyeTool.attemptToDyeBlock(context);

            if(result != ActionResult.PASS){
                cir.setReturnValue(result);
            }
        }
    }
}
