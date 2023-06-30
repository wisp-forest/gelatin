package io.wispforest.jello.mixins.client.owo;

import io.wispforest.owo.ui.util.NinePatchRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = NinePatchRenderer.class, remap = false)
public interface NinePatchRendererAccessor {

    @Accessor("u") int getU();
    @Accessor("v") int getV();

    @Mutable @Accessor("u") void setU(int u);
    @Mutable @Accessor("v") void setV(int v);
}
