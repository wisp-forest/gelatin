package io.wispforest.gelatin.dye_entries.client;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_entries.mixins.AnimationAccessor;
import io.wispforest.gelatin.dye_entries.mixins.client.SpriteContentsAccessor;
import io.wispforest.gelatin.dye_entries.utils.GrayScaleBlockRegistry;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;

public class GrayScaledSpriteInfo extends SpriteContents {

    private final Identifier defaultTextureId;

    public GrayScaledSpriteInfo(Identifier grayscaleId, Identifier defaultTextureId, int width, int height, NativeImage image, SpriteContents.Animation animation) {
        super(grayscaleId, new SpriteDimensions(width, height), ColorUtil.convertImageToGrayScale(image), AnimationResourceMetadata.EMPTY);

        var accessor = ((AnimationAccessor) animation);

        ((SpriteContentsAccessor) this)
                .jello$setAnimation(
                        AnimationAccessor.gelatin$createAnimation(this, accessor.gelatin$getFrames(), accessor.gelatin$getFrameCount(), accessor.gelatin$isInterpolation())
                );

        this.defaultTextureId = defaultTextureId;
    }

    public static GrayScaledSpriteInfo of(SpriteContents info){
        var accessor = ((SpriteContentsAccessor)info);

        return new GrayScaledSpriteInfo(GrayScaleBlockRegistry.INSTANCE.createGrayScaleID(info.getId()), info.getId(), info.getWidth(), info.getHeight(), accessor.jello$getImage(), accessor.jello$getAnimation());
    }

    public static GrayScaledSpriteInfo of(Identifier id, int width, int height, NativeImage image, SpriteContents.Animation animation){
        return new GrayScaledSpriteInfo(GrayScaleBlockRegistry.INSTANCE.createGrayScaleID(id), id, width, height, image, animation);
    }

    public Identifier getDefaultTextureId(){
        return defaultTextureId;
    }


}
