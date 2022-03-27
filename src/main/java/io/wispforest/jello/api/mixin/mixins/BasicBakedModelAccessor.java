package io.wispforest.jello.api.mixin.mixins;

import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BasicBakedModel.class)
public interface BasicBakedModelAccessor {
    @Accessor
    Sprite getSprite();
}
