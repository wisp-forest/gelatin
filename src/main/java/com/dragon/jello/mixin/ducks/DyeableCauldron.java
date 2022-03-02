package com.dragon.jello.mixin.ducks;

import com.dragon.jello.dyelib.DyeColorRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;

public interface DyeableCauldron {

    IntProperty DYE_COLOR = IntProperty.of("dye_color", 0, 16);

    static DyeColorRegistry.DyeColor getDyeColor(BlockState state){
        return isWaterColored(state) ? DyeColorRegistry.DyeColor.byOldDyeId(state.get(DYE_COLOR)) : null;
    }

    static boolean isWaterColored(BlockState state){
        return state.get(DYE_COLOR) < 16;
    }
}
