package com.dragon.jello.common.blocks;

import com.dragon.jello.dyelib.DyeColorRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public class SlimeSlabColored extends SlimeSlab implements BlockColorProvider, DyeableBlock {
    private int blockColor;
    private final DyeColorRegistry.DyeColor dyeColor;

    public SlimeSlabColored(DyeColorRegistry.DyeColor dyeColor, Settings settings) {
        super(settings.mapColor(dyeColor.getMapColor()));

        float[] colorComp = dyeColor.getColorComponents();

        this.blockColor = new Color(colorComp[0], colorComp[1], colorComp[2], 1.0F).getRGB();
        this.dyeColor = dyeColor;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        return blockColor;
    }

    @Override
    public int getBlockColor(){
        return blockColor;
    }

    @Override
    public DyeColorRegistry.DyeColor getDyeColor(){
        return dyeColor;
    }
}
