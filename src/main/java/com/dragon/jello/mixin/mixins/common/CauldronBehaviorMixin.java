package com.dragon.jello.mixin.mixins.common;

import com.dragon.jello.mixin.ducks.DyeableCauldron;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {

    @Inject(method = "method_32209", at = @At("HEAD"), cancellable = true)
    private static void CLEAN_DYEABLE_ITEM$cancelIfDyedWater(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> cir){
        if(DyeableCauldron.isWaterColored(state)){
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "method_32214", at = @At("HEAD"), cancellable = true)
    private static void CLEAN_BANNER$cancelIfDyedWater(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> cir){
        if(DyeableCauldron.isWaterColored(state)){
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "method_32215", at = @At("HEAD"), cancellable = true)
    private static void CLEAN_SHULKER_BOX$cancelIfDyedWater(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> cir){
        if(DyeableCauldron.isWaterColored(state)){
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
