package io.wispforest.jello.mixin.dye;

import io.wispforest.jello.misc.ducks.DyeBlockStorage;
import io.wispforest.jello.block.colored.ColoredGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

    @Unique
    private static BlockPos currentBlockCheckPos;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void cacheBlockPos(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci, int i, int j, int k, BlockPos blockPos, BeaconBlockEntity.BeamSegment beamSegment, int l, int m) {
        currentBlockCheckPos = blockPos;
    }

    @ModifyVariable(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/DyeColor;getColorComponents()[F"))
    private static float[] allowForCustomDyeColor(float[] fs, World world, BlockPos blockPos, BlockState blockState, BeaconBlockEntity beaconBlockEntity) {
        if (world.getBlockState(currentBlockCheckPos).getBlock() instanceof ColoredGlassBlock coloredGlassBlock) {
            return ((DyeBlockStorage) coloredGlassBlock).getDyeColor().getColorComponents();
        }

        return fs;
    }
}
