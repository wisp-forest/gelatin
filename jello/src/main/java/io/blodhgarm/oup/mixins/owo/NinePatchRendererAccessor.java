package io.blodhgarm.oup.mixins.owo;

import io.wispforest.owo.ui.util.NinePatchTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = NinePatchTexture.class, remap = false)
public interface NinePatchRendererAccessor {

    @Accessor("u") int getU();
    @Accessor("v") int getV();

    @Mutable @Accessor("u") void setU(int u);
    @Mutable @Accessor("v") void setV(int v);
}
