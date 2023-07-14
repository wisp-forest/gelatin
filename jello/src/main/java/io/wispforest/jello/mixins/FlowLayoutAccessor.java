package io.wispforest.jello.mixins;

import io.wispforest.owo.ui.container.FlowLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FlowLayout.class)
public interface FlowLayoutAccessor {
    @Accessor("algorithm") FlowLayout.Algorithm jello$getAlgorithm();
}
