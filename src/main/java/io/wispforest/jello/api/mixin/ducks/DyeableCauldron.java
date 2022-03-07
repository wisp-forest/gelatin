package io.wispforest.jello.api.mixin.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;

public interface DyeableCauldron {

    IntProperty DYE_COLOR = IntProperty.of("dye_color", 0, 16);

    static DyeColorant getDyeColor(BlockState state){
        return isWaterColored(state) ? DyeColorant.byOldIntId(state.get(DYE_COLOR)) : null;
    }

    static boolean isWaterColored(BlockState state){
        return state.get(DYE_COLOR) < 16;
    }
}
