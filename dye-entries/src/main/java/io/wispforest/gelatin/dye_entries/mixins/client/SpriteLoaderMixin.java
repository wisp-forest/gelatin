package io.wispforest.gelatin.dye_entries.mixins.client;

import com.mojang.logging.LogUtils;
import io.wispforest.gelatin.dye_entries.client.GrayScaledSpriteInfo;
import io.wispforest.gelatin.dye_entries.utils.GrayScaleBlockRegistry;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {

    @Shadow @Final private Identifier id;

    @Unique
    @Final private static Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "stitch", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    private void jello$createGrayScaledSprites(List<SpriteContents> sprites, int mipLevel, Executor executor, CallbackInfoReturnable<SpriteLoader.StitchResult> cir, int i, TextureStitcher<SpriteContents> textureStitcher, int j, int k){
        if(!this.id.equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)) return;

        List<TextureStitcher.Holder<SpriteContents>> holders = ((TextureStitcherAccessor<SpriteContents>)textureStitcher).gelatin$getHolders();

        for(Identifier identifier : GrayScaleBlockRegistry.GRAYSCALABLE_BLOCK_SPRITES){
            Optional<TextureStitcher.Holder<SpriteContents>> spriteData = holders.stream().filter(holder -> holder.sprite().getId().equals(identifier)).findAny();

            if(spriteData.isPresent()){
                textureStitcher.add(GrayScaledSpriteInfo.of(spriteData.get().sprite()));
            } else {
                textureStitcher.add(GrayScaledSpriteInfo.of(identifier, 0, 0, new NativeImage(0,0, false), null));
                LOGGER.error("[GrayScaleSpriteInject]: Using missing texture, sprite {} not found", identifier);
            }
        }
        //System.out.println("test injection");
    }
}
