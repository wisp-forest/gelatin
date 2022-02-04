package com.dragon.jello.mixin.mixins;

import com.dragon.jello.blocks.SlimeBlockColored;
import com.dragon.jello.tags.JelloBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {

    @Inject(method = "isBlockSticky", at = @At(value = "HEAD"), cancellable = true)
    private static void isBlockStickyExt(BlockState state, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(state.isIn(JelloBlockTags.STICKY_BLOCKS));
    }

    @Inject(method = "isAdjacentBlockStuck", at = @At(value = "HEAD"), cancellable = true)
    private static void isAdjacentBlockStuckExt(BlockState state, BlockState adjacentState, CallbackInfoReturnable<Boolean> cir){
        if(dyeColorComparisionTest(state, adjacentState)){
            cir.setReturnValue(false);
        } else if(customSlimeBlockTest(state, adjacentState)){
            cir.setReturnValue(false);
        } else if (state.isOf(Blocks.HONEY_BLOCK) && adjacentState.isOf(Blocks.SLIME_BLOCK)) {
            cir.setReturnValue(false);
        } else if (state.isOf(Blocks.SLIME_BLOCK) && adjacentState.isOf(Blocks.HONEY_BLOCK)) {
            cir.setReturnValue(false);
        } else {
            cir.setReturnValue(PistonHandlerAccessor.callIsBlockSticky(state) || PistonHandlerAccessor.callIsBlockSticky(adjacentState));
        }
    }

    private static boolean dyeColorComparisionTest(BlockState state, BlockState adjacentState){
        if(state.getBlock() instanceof SlimeBlockColored slimeBlockColored && adjacentState.getBlock() instanceof SlimeBlockColored slimeBlockColoredAdj){
            return slimeBlockColored.getDyeColor() != slimeBlockColoredAdj.getDyeColor();
        } else{
            return false;
        }
    }

    private static boolean customSlimeBlockTest(BlockState state, BlockState adjacentState){
        if(state.isIn(JelloBlockTags.COLORED_SLIME_BLOCKS) && (adjacentState.isOf(Blocks.HONEY_BLOCK) || adjacentState.isOf(Blocks.SLIME_BLOCK))){
            return true;
        }else if(adjacentState.isIn(JelloBlockTags.COLORED_SLIME_BLOCKS) && (state.isOf(Blocks.HONEY_BLOCK) || state.isOf(Blocks.SLIME_BLOCK))){
            return true;
        }else{
            return false;
        }
    }


}
