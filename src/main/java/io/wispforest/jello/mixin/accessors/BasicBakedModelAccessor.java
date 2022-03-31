package io.wispforest.jello.mixin.accessors;

import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BasicBakedModel.class)
public interface BasicBakedModelAccessor {
    @Accessor("sprite")
    Sprite jello$getSprite();
}
