package io.wispforest.jello.api.mixin.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.blockentity.ColorStorageBlockEntity;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.IntProperty;

public interface DyeableCauldron {

    static boolean isWaterColored(ColorStorageBlockEntity blockEntity){
        return blockEntity.getDyeColorant() != DyeColorRegistry.NULL_VALUE_NEW;
    }
}
