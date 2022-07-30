package io.wispforest.jello.client;

import io.wispforest.jello.api.registry.GrayScaleBlockRegistry;
import io.wispforest.jello.mixin.client.accessors.SpriteInfoAccessor;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public class GrayScaledSpriteInfo extends Sprite.Info{

    private final Identifier defaultTextureId;

    public GrayScaledSpriteInfo(Identifier grayscaleId, Identifier defaultTextureId, int width, int height, AnimationResourceMetadata animationData) {
        super(grayscaleId, width, height, animationData);

        this.defaultTextureId = defaultTextureId;
    }

    public static GrayScaledSpriteInfo of(Sprite.Info info){
        return new GrayScaledSpriteInfo(GrayScaleBlockRegistry.INSTANCE.createGrayScaleID(info.getId()), info.getId(), info.getWidth(), info.getHeight(), ((SpriteInfoAccessor)info).jello$getAnimationData());
    }

    public static GrayScaledSpriteInfo of(Identifier id, int width, int height, AnimationResourceMetadata animationData){
        return new GrayScaledSpriteInfo(GrayScaleBlockRegistry.INSTANCE.createGrayScaleID(id), id, width, height, animationData);
    }

    public Identifier getDefaultTextureId(){
        return defaultTextureId;
    }


}
