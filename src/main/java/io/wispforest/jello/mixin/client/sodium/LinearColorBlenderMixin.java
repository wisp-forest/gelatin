package io.wispforest.jello.mixin.client.sodium;

import io.wispforest.jello.blockentity.ColorStorageBlockEntity;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.util.color.ColorARGB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(targets = "me/jellysquid/mods/sodium/client/model/quad/blender/LinearColorBlender")
@Pseudo
public class LinearColorBlenderMixin  {

    @Inject(method = "getColors", at = @At(value = "HEAD"), cancellable = true)
    public <T> void jello$redirectToLinears(BlockRenderView world, BlockPos origin, ModelQuadView quad, ColorSampler<T> sampler, T state, CallbackInfoReturnable<int[]> cir){
        if(world.getBlockEntity(origin) instanceof ColorStorageBlockEntity){
            int[] colorArray = new int[4];

            Arrays.fill(colorArray, ColorARGB.toABGR(sampler.getColor(state, world, origin, quad.getColorIndex())));
            cir.setReturnValue(colorArray);
        }
    }
}
