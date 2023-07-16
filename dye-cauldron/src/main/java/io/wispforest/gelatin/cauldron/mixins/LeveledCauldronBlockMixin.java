package io.wispforest.gelatin.cauldron.mixins;

import io.wispforest.gelatin.cauldron.blockentity.ColorStorageBlockEntity;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(LeveledCauldronBlock.class)
@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public abstract class LeveledCauldronBlockMixin extends AbstractCauldronBlock implements BlockEntityProvider, BlockColorProvider {

    @Shadow public boolean isFull(BlockState state) { return false; }

    public LeveledCauldronBlockMixin(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ColorStorageBlockEntity(pos, state);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        if(world == null || pos == null) return -1;

        int worldColor = BiomeColors.getWaterColor(world, pos);

        if (world.getBlockEntity(pos) instanceof ColorStorageBlockEntity colorBlockEntity && colorBlockEntity.getDyeColorant() != DyeColorantRegistry.NULL_VALUE_NEW) {
            float[] colorComp = {1F, 1F, 1F};

            DyeColorant dyeColor = colorBlockEntity.getDyeColorant();

            if (dyeColor != null) colorComp = dyeColor.getColorComponents();

            return (int) (colorComp[0] * 255) << 16 | (int) (colorComp[1] * 255) << 8 | (int) (colorComp[2] * 255);
        }

        return worldColor;

    }
}
