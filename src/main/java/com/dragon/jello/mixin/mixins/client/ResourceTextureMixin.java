package com.dragon.jello.mixin.mixins.client;

import com.dragon.jello.Util.ColorStateManager;
import com.dragon.jello.registry.ColorizeRegistry;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Debug(export = true)
@Mixin(ResourceTexture.class)
public abstract class ResourceTextureMixin extends AbstractTexture {
//    @Unique private static final Logger CURSED_LOGGER = LogManager.getLogger("Test");
//    @Unique private static final Set<String> MAIN_BLACK_LIST = new HashSet<>();
//    @Unique private static final Set<String> NAME_VARIANT_BLACK_LIST = new HashSet<>();
//    @Unique private int glIdCallCount = 0;
//
//    @Unique private static int indexResourceNumber = 1;
//    @Unique private int uniqueID = 0;
//
//    static{
//        MAIN_BLACK_LIST.addAll(Arrays.stream(new String[]{"banner", "shield", "signs", "projectiles", "bed", "chest", "conduit", "bell"}).toList());
//        NAME_VARIANT_BLACK_LIST.addAll(Arrays.stream(new String[]{"shulker", "fishing", "experience", "enchanting", "beacon", "end", "lead", "banner", "shield"}).toList());
//    }
//
//    //--------------------------------------------------------------------//
//
//    @Unique protected int glIdGrayScale = -1;
//    @Unique protected boolean hasGrayScaleVar = false;
//
//    @Shadow @Final protected Identifier location;
//
//    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/ResourceTexture$TextureData;getImage()Lnet/minecraft/client/texture/NativeImage;", shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
//    private void injectTest(ResourceManager manager, CallbackInfo ci, ResourceTexture.TextureData textureData, boolean bl, boolean bl2, NativeImage nativeImage) {
//        if(isMinecraftSpecficBlacklisted(location) && ColorizeRegistry.isRegistered(location)){
//            if (!RenderSystem.isOnRenderThreadOrInit()) {
//                RenderSystem.recordRenderCall(() -> uploadDouble(nativeImage, bl, bl2));
//            }
//            uploadDouble(nativeImage, bl, bl2);
//
//            ci.cancel();
//        }
//
//        uniqueID = indexResourceNumber;
//        indexResourceNumber = indexResourceNumber + 1;
//    }
//
//    @Unique
//    public final void uploadDouble(NativeImage image, boolean blur, boolean clamp) {
//        NativeImage imageCopy = generateGrayScaleImage(image);
//        ((ResourceTexture)(Object)this).upload(image, blur, clamp);
//        uploadGrayScale(imageCopy, blur, clamp);
//    }
//
//    @Unique
//    public final void uploadGrayScale(NativeImage imageCopy, boolean blur, boolean clamp) {
//        TextureUtil.prepareImage(this.getGlIdGrayScale(), 0, imageCopy.getWidth(), imageCopy.getHeight());
//        imageCopy.upload(0, 0, 0, 0, 0, imageCopy.getWidth(), imageCopy.getHeight(), blur, clamp, false, true);
//        this.hasGrayScaleVar = true;
//    }
//
//    @Unique
//    public int getGlIdGrayScale() {
//        RenderSystem.assertOnRenderThreadOrInit();
//        if (this.glIdGrayScale == -1) {
//            this.glIdGrayScale = TextureUtil.generateTextureId();
//        }
//
//        return this.glIdGrayScale;
//    }
//
//    //--------------------------------------------------------------------//
//
//    @Override
//    public int getGlId() {
//        int nonumberhere = uniqueID;
//
//        if(pollGrayScale() && this.hasGrayScaleVar){
//            RenderSystem.assertOnRenderThreadOrInit();
//            if (this.glIdGrayScale == -1) {
//                this.glIdGrayScale = TextureUtil.generateTextureId();
//            }
//
////            grayScaleClearCount++;
////            if(grayScaleClearCount == 2){
////                grayScaleClearCount = 0;
////                ColorStateManager.GRAY_SCALE_TEST.pollLast();
////            }
//
//            return this.glIdGrayScale;
//        }else{
//            RenderSystem.assertOnRenderThreadOrInit();
//            if (this.glId == -1) {
//                this.glId = TextureUtil.generateTextureId();
//            }
//
//            return this.glId;
//        }
//    }
//
//    @Override
//    public void clearGlId() {
//        if (!RenderSystem.isOnRenderThread()) {
//            RenderSystem.recordRenderCall(() -> {
//                if (this.glId != -1) {
//                    TextureUtil.releaseTextureId(this.glId);
//                    this.glId = -1;
//                }
//                if(this.hasGrayScaleVar && this.glIdGrayScale != -1){
//                    TextureUtil.releaseTextureId(this.glIdGrayScale);
//                    this.glIdGrayScale = -1;
//                }
//            });
//        }
//        else {
//            if (this.glId != -1) {
//                TextureUtil.releaseTextureId(this.glId);
//                this.glId = -1;
//            }
//            if(this.hasGrayScaleVar && this.glIdGrayScale != -1){
//                TextureUtil.releaseTextureId(this.glIdGrayScale);
//                this.glIdGrayScale = -1;
//            }
//        }
//
//        ColorStateManager.GRAY_SCALE_TEST.clear();
//    }
//
//    //--------------------------------------------------------------------//
//
//    @Unique
//    private static boolean isBlackListed(Identifier identifier){
//        String[] pathComponents = identifier.getPath().split("/");
//        String name = pathComponents[pathComponents.length-1].split("\\.")[0];
//
//        //TODO: Implement player rendering fixes at a later date
//        if(Objects.equals(name, "alex") || Objects.equals(name, "steve")){
//            return true;
//        }
//
//        //First use generic black list
//        if(MAIN_BLACK_LIST.contains(pathComponents[2]) && pathComponents.length == 5 && pathComponents[3].matches("decor|armor")){
//            //More Targeted removal
//            String[] nameSplit = name.split("_");
//            if(nameSplit.length >= 2 && (NAME_VARIANT_BLACK_LIST.contains(nameSplit[0]) || nameSplit[nameSplit.length-1].equals("grayscale"))){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Unique
//    private static boolean isMinecraftSpecficBlacklisted(Identifier identifier){
//        if(identifier.getNamespace().equals("minecraft")){
//            String[] pathComponents = identifier.getPath().split("/");
//            if(Arrays.asList(pathComponents).contains("entity")){
////                CURSED_LOGGER.info("A Minecraft entity was found:" + identifier);
//                if(!isBlackListed(identifier)) {
////                    CURSED_LOGGER.info("And passed the blacklist:" + identifier);
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    @Unique
//    private NativeImage generateGrayScaleImage(NativeImage nativeImage){
//        NativeImage imageCopy = new NativeImage(nativeImage.getWidth(),nativeImage.getHeight(),false);
//        imageCopy.copyFrom(nativeImage);
//
//        convertImageToGrayScale(imageCopy);
//
//        return imageCopy;
//    }
//
//    @Unique
//    private void convertImageToGrayScale(NativeImage nativeImage){
//        long pointer = nativeImage.pointer;
//
//        final IntBuffer buffer = MemoryUtil.memIntBuffer(pointer, (nativeImage.getWidth() * nativeImage.getHeight()));
//        int[] pixelColors = new int[buffer.remaining()];
//        buffer.get(pixelColors);
//        buffer.clear();
//
//        for (int index = 0; index < pixelColors.length; index++) {
//            int currentColor = pixelColors[index];
//            int a1 = (currentColor >> 24) & 0xff;
//            if(a1 != 0){
//                int r1 = (currentColor >> 16) & 0xFF;
//                int g1 = (currentColor >> 8) & 0xFF;
//                int b1 = (currentColor >> 0) & 0xFF;
//
//                int grayValue = (r1 + g1 + b1) / 3;
//
//                pixelColors[index] = ((a1 & 0xFF) << 24) | ((grayValue & 0xFF) << 16) | ((grayValue & 0xFF) << 8)  | ((grayValue & 0xFF) << 0);
//            }
//        }
//
//        buffer.put(pixelColors);
//        buffer.clear();
//    }
//
//    @Unique
//    private Boolean pollGrayScale(){
//        if(!ColorStateManager.GRAY_SCALE_TEST.isEmpty()) {
//            glIdCallCount = glIdCallCount + 1;
//            if (glIdCallCount == 2) {
//                glIdCallCount = 0;
//                return ColorStateManager.GRAY_SCALE_TEST.pollLast();
//            }
//
//            return ColorStateManager.GRAY_SCALE_TEST.peekLast();
//        }
//
//        return false;
//    }


}
