package com.dragon.jello.api.mixin.mixins.cauldron;

import com.dragon.jello.api.dye.DyeColorant;
import com.dragon.jello.api.mixin.ducks.DyeableCauldron;
import com.dragon.jello.api.mixin.mixins.accessors.BlockAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(LeveledCauldronBlock.class)
@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public class LeveledCauldronBlockMixin implements BlockColorProvider, DyeableCauldron {

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        int worldColor = world != null && pos != null ? BiomeColors.getWaterColor(world, pos) : -1;

        if(DyeableCauldron.isWaterColored(state)){
            DyeColorant dyeColor = DyeableCauldron.getDyeColor(state);
            float[] colorComp = {1F,1F,1F};

            if(dyeColor != null){
                colorComp = dyeColor.getColorComponents();
            }

            return (int)(colorComp[0] * 255) << 16 | (int)(colorComp[1] * 255) << 8 | (int)(colorComp[2] * 255);
        }

        return worldColor;
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void addDyeColorPropertie(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci){
        builder.add(DYE_COLOR);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void adjustDefaultBlockState(AbstractBlock.Settings settings, Predicate precipitationPredicate, Map behaviorMap, CallbackInfo ci){
        ((BlockAccessor)this).callSetDefaultState(((LeveledCauldronBlock)(Object)this).getStateManager().getDefaultState().with(LeveledCauldronBlock.LEVEL, Integer.valueOf(1)).with(DYE_COLOR, Integer.valueOf(16)));
    }


}
