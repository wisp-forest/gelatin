package io.wispforest.gelatin.dye_entries.mixins.client;

import com.mojang.logging.LogUtils;
import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_entries.client.GrayScaledSpriteInfo;
import io.wispforest.gelatin.dye_entries.utils.GrayScaleBlockRegistry;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin {

    @Shadow @Final private Identifier id;

    @Shadow protected abstract Identifier getTexturePath(Identifier id);

    @Shadow public abstract Identifier getId();

    @Unique @Final private static Logger LOGGER = LogUtils.getLogger();

    @Unique private final ThreadLocal<GrayScaledSpriteInfo> cachedInfo = ThreadLocal.withInitial(() -> null);

    @Inject(method = "stitch", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    private void jello$createGrayScaledSprites(ResourceManager resourceManager, Stream<Identifier> idStream, Profiler profiler, int mipmapLevel, CallbackInfoReturnable<SpriteAtlasTexture.Data> cir, Set<Identifier> set, int i, TextureStitcher textureStitcher, int j, int k){
        if(this.id.equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)) {
            Set<TextureStitcher.Holder> holders = ((TextureStitcherAccessor)textureStitcher).getHolders();

            for(Identifier identifier : GrayScaleBlockRegistry.GRAYSCALABLE_BLOCK_SPRITES){
                Optional<TextureStitcher.Holder> spriteData = holders.stream().filter(holder -> holder.sprite.getId().equals(identifier)).findAny();

                if(spriteData.isPresent()){
                    textureStitcher.add(GrayScaledSpriteInfo.of(spriteData.get().sprite));
                } else {
                    textureStitcher.add(GrayScaledSpriteInfo.of(identifier, 0, 0, AnimationResourceMetadata.EMPTY));
                    LOGGER.error("[GrayScaleSpriteInject]: Using missing texture, sprite {} not found", identifier);
                }
            }

            System.out.println("test injection");
        }
    }

    @Inject(method = "loadSprite", at = @At("HEAD"))
    private void jello$changeIdentifierCall(ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y, CallbackInfoReturnable<Sprite> cir){
        if(info instanceof GrayScaledSpriteInfo grayScaledSpriteInfo) {
            cachedInfo.set(grayScaledSpriteInfo);
        } else {
            cachedInfo.set(null);
        }
    }

    @ModifyArg(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;getTexturePath(Lnet/minecraft/util/Identifier;)Lnet/minecraft/util/Identifier;"))
    private Identifier jello$changeIdentifierCall(Identifier value){
        if(cachedInfo.get() != null){
            return cachedInfo.get().getDefaultTextureId();
        } else {
            return value;
        }
    }

    @ModifyArg(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/Sprite;<init>(Lnet/minecraft/client/texture/SpriteAtlasTexture;Lnet/minecraft/client/texture/Sprite$Info;IIIIILnet/minecraft/client/texture/NativeImage;)V"))
    private NativeImage jello$GrayscaleImageCall(NativeImage value){
        if(cachedInfo.get() != null){
            return ColorUtil.convertImageToGrayScale(value);
        } else {
            return value;
        }
    }

//    @Inject(method = "upload", at = @At("TAIL"))
//    private void saveBlockAtlas(SpriteAtlasTexture.Data data, CallbackInfo ci){
//        if(this.getId().equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)) {
//            SpriteAtlasTextureDataAccessor accessor = ((SpriteAtlasTextureDataAccessor) data);
//
//            //ByteBuffer pixels = ByteBuffer.wrap(new byte[accessor.jello$getHeight() * accessor.jello$getWidth()]);
//
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((SpriteAtlasTexture) (Object) this).getGlId());
//
//            //GL11.glGetTexImage(((SpriteAtlasTexture) (Object) this).getGlId(), 0, GL11.GL_RGBA, GL11.GL_BYTE, pixels);
//
//            try {
//                NativeImage nativeImage = new NativeImage(accessor.jello$getWidth(), accessor.jello$getHeight(), true);//NativeImage.read(NativeImage.Format.RGBA, pixels);
//
//                nativeImage.loadFromTextureImage(0, false);
//
//                nativeImage.writeTo("atlast_shit\\block.png");
//
//                LOGGER.info("Atlas has been dumped!");
//            } catch (Exception e) {
//                LOGGER.error(e);
//                LOGGER.info("Atlas has failed!");
//            }
//        }
//    }
}
