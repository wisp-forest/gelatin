package io.wispforest.jello.block;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public class SlimeSlabColored extends SlimeSlab implements BlockColorProvider {

    public SlimeSlabColored(DyeColorant dyeColor, Settings settings) {
        super(settings.mapColor(dyeColor.getMapColor()));

        this.setDyeColor(dyeColor);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        return this.getDyeColorant().getBaseColor();
    }
}
