package io.wispforest.jello.mixin.dye;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.ducks.DyeItemStorage;
import io.wispforest.jello.api.dye.events.ColorBlockEventMethods;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void jello$useDyeItemStorageFirst(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir, PlayerEntity playerEntity, BlockPos blockPos, CachedBlockPosition cachedBlockPosition, Item item){
        if(item instanceof DyeItemStorage dyeItemStorage){
            ActionResult result = dyeItemStorage.attemptToDyeBlock(context);

            if(result != ActionResult.PASS){
                cir.setReturnValue(result);
            }
        }
    }
}
