package com.dragon.jello.mixin.mixins;

import com.dragon.jello.PistonHandlerRewrite;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
//    @Redirect(method = "tryMove", at = @At(value = "NEW", target = "Lnet/minecraft/block/piston/PistonHandler;<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/piston/PistonHandler;"))
//    private PistonHandler redirectCreatePistonHandler1(World world, BlockPos pos, Direction dir, boolean retracted){
//        return new PistonHandlerRewrite(world, pos, dir, retracted);
//    }
//
//    @Redirect(method = "move", at = @At(value = "NEW", target = "Lnet/minecraft/block/piston/PistonHandler;<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/piston/PistonHandler;"))
//    private PistonHandler redirectCreatePistonHandler2(World world, BlockPos pos, Direction dir, boolean retracted){
//        return new PistonHandlerRewrite(world, pos, dir, retracted);
//    }
}
