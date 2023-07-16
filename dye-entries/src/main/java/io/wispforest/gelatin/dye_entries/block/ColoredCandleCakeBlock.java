package io.wispforest.gelatin.dye_entries.block;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public class ColoredCandleCakeBlock extends CandleCakeBlock implements BlockColorProvider {
    public ColoredCandleCakeBlock(DyeColorant dyeColorant, Block candle, Settings settings) {
        super(candle, settings);

        ((DyeBlockStorage) this).setDyeColor(dyeColorant);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        //Small patch to fix particles being tinted
        return tintIndex == 0 ? -1 : this.getDyeColorant().getBaseColor();
    }
}
