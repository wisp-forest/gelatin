package com.dragon.jello.mixin.mixins.common.accessors;

import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PistonHandler.class)
public interface PistonHandlerAccessor {
    @Invoker
    static boolean callIsBlockSticky(BlockState state) {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static boolean callIsAdjacentBlockStuck(BlockState state, BlockState adjacentState) {
        throw new UnsupportedOperationException();
    }
}
