package io.wispforest.dye_entries.block;

import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.ducks.DyeBlockStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.DyedCarpetBlock;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public class ColoredCarpetBlock extends DyedCarpetBlock implements BlockColorProvider {

    /**
     * @param dyeColorant the color of this carpet when worn by a {@linkplain net.minecraft.entity.passive.LlamaEntity llama}
     */
    public ColoredCarpetBlock(Settings settings, DyeColorant dyeColorant) {
        super(null, settings);

        ((DyeBlockStorage) this).setDyeColor(dyeColorant);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        DyeColorant dyeColorant = ((DyeBlockStorage) this).getDyeColorant();

        return dyeColorant.getBaseColor();
    }
}