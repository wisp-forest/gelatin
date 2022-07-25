package io.wispforest.jello.mixin.client.accessors;

import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Sprite.Info.class)
public interface SpriteInfoAccessor {

    @Accessor("animationData")
    AnimationResourceMetadata jello$getAnimationData();
}
