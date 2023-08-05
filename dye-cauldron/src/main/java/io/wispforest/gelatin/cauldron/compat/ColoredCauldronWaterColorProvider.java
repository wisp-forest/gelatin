package io.wispforest.gelatin.cauldron.compat;

import io.wispforest.gelatin.cauldron.blockentity.ColorStorageBlockEntity;
import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

public class ColoredCauldronWaterColorProvider<T> implements ColorProvider<T> {

    private final ColorProvider<T> alternativeProvider;

    public ColoredCauldronWaterColorProvider(ColorProvider<T> alternativeProvider){
        this.alternativeProvider = alternativeProvider;
    }

    @Override
    public void getColors(WorldSlice view, BlockPos pos, T state, ModelQuadView quad, int[] output) {
        if(view.getBlockEntity(pos) instanceof ColorStorageBlockEntity entity && entity.isDyed() && entity.getCachedState().getBlock() instanceof BlockColorProvider provider){
            Arrays.fill(output, ColorARGB.toABGR(provider.getColor((BlockState) state, view, pos, quad.getColorIndex())));

            return;
        }

        alternativeProvider.getColors(view, pos, state, quad, output);
    }
}
