package io.wispforest.gelatin.dye_entries.mixins.client;

import com.mojang.logging.LogUtils;
import io.wispforest.gelatin.common.util.GrayScaleUtils;
import io.wispforest.gelatin.dye_entries.client.GrayScaledSpriteInfo;
import io.wispforest.gelatin.dye_entries.utils.GrayScaleBlockRegistry;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.*;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin {

//    @Shadow @Final private Identifier id;
//
//    @Shadow protected abstract Identifier getTexturePath(Identifier id);
//
//    @Shadow public abstract Identifier getId();
//
//    @Unique private final ThreadLocal<GrayScaledSpriteInfo> cachedInfo = ThreadLocal.withInitial(() -> null);

//    @Inject(method = "loadSprite", at = @At("HEAD"))
//    private void jello$changeIdentifierCall(ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y, CallbackInfoReturnable<Sprite> cir){
//        cachedInfo.set(info instanceof GrayScaledSpriteInfo grayInfo ? grayInfo : null);
//    }
//
//    @ModifyArg(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;getTexturePath(Lnet/minecraft/util/Identifier;)Lnet/minecraft/util/Identifier;"))
//    private Identifier jello$changeIdentifierCall(Identifier value){
//        return cachedInfo.get() != null
//                ? cachedInfo.get().getDefaultTextureId()
//                : value;
//    }
//
//    @ModifyArg(method = "loadSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/Sprite;<init>(Lnet/minecraft/client/texture/SpriteAtlasTexture;Lnet/minecraft/client/texture/Sprite$Info;IIIIILnet/minecraft/client/texture/NativeImage;)V"))
//    private NativeImage jello$GrayscaleImageCall(NativeImage value){
//        return cachedInfo.get() != null
//                ? GrayScaleUtils.convertImageToGrayScale(value)
//                : value;
//    }

    //ATLAS DUMPING METHOD!!!!
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
