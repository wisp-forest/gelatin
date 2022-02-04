package com.dragon.jello.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class SlimeBlockColored extends SlimeBlock implements BlockColorProvider {
    private int blockColor;
    private final DyeColor dyeColor;

    public SlimeBlockColored(DyeColor dyeColor, Settings settings) {
        super(settings);

        float[] colorComp = dyeColor.getColorComponents();

        this.blockColor = new Color(colorComp[0], colorComp[1], colorComp[2], 1.0F).getRGB();
        this.dyeColor = dyeColor;
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        return blockColor;
    }

    public int getBlockColor(){
        return blockColor;
    }

    public DyeColor getDyeColor(){
        return dyeColor;
    }
}
