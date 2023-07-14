package io.wispforest.gelatin.dye_entries.mixins.client;

import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.class)
public interface SpriteContentsAccessor {
    @Accessor("animation") void jello$setAnimation(SpriteContents.Animation animation);
    @Accessor("animation") SpriteContents.Animation jello$getAnimation();
    @Accessor("image") NativeImage jello$getImage();
}
