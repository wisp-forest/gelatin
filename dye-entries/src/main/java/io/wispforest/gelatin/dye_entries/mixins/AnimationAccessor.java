package io.wispforest.gelatin.dye_entries.mixins;

import net.minecraft.client.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(SpriteContents.Animation.class)
public interface AnimationAccessor {

    @Accessor("frames") List<SpriteContents.AnimationFrame> gelatin$getFrames();
    @Accessor("frameCount") int gelatin$getFrameCount();
    @Accessor("interpolation") boolean gelatin$isInterpolation();

    @Invoker("<init>")
    static SpriteContents.Animation gelatin$createAnimation(SpriteContents contents, List<SpriteContents.AnimationFrame> frames, int frameCount, boolean interpolation) {
        throw new UnsupportedOperationException();
    }
}
