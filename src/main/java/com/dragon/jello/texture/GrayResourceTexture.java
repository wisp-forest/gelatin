package com.dragon.jello.texture;

import com.dragon.jello.Util.ColorStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.nio.IntBuffer;

public class GrayResourceTexture extends ResourceTexture {

    private final Identifier orignalResourceID;
    private final ResourceTexture orignalTexture;

    public GrayResourceTexture(Identifier location, ResourceTexture texture) {
        super(location);
        orignalResourceID = texture.location;
        orignalTexture = texture;
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        ResourceTexture.TextureData textureData = orignalTexture.loadTextureData(manager);
        //ResourceTexture.TextureData textureData = ((ResourceImpl)manager.getResource(orignalResourceID)).loadTextureData(manager);
        textureData.checkException();
        TextureResourceMetadata textureResourceMetadata = textureData.getMetadata();
        boolean bl;
        boolean bl2;
        if (textureResourceMetadata != null) {
            bl = textureResourceMetadata.shouldBlur();
            bl2 = textureResourceMetadata.shouldClamp();
        } else {
            bl = false;
            bl2 = false;
        }

        NativeImage nativeImage = generateGrayScaleImage(textureData.getImage());
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.upload(nativeImage, bl, bl2));
        } else {
            this.upload(nativeImage, bl, bl2);
        }
    }

    private NativeImage generateGrayScaleImage(NativeImage nativeImage){
        NativeImage imageCopy = new NativeImage(nativeImage.getWidth(),nativeImage.getHeight(),false);
        imageCopy.copyFrom(nativeImage);

        convertImageToGrayScale(imageCopy);

        return imageCopy;
    }

    private void convertImageToGrayScale(NativeImage nativeImage){
        long pointer = nativeImage.pointer;

        final IntBuffer buffer = MemoryUtil.memIntBuffer(pointer, (nativeImage.getWidth() * nativeImage.getHeight()));
        int[] pixelColors = new int[buffer.remaining()];
        buffer.get(pixelColors);
        buffer.clear();

        for (int index = 0; index < pixelColors.length; index++) {
            int currentColor = pixelColors[index];
            int a1 = (currentColor >> 24) & 0xff;
            if(a1 != 0){
                int r1 = (currentColor >> 16) & 0xFF;
                int g1 = (currentColor >> 8) & 0xFF;
                int b1 = (currentColor >> 0) & 0xFF;

                int grayValue = (r1 + g1 + b1) / 3;

                pixelColors[index] = ((a1 & 0xFF) << 24) | ((grayValue & 0xFF) << 16) | ((grayValue & 0xFF) << 8)  | ((grayValue & 0xFF) << 0);
            }
        }

        buffer.put(pixelColors);
        buffer.clear();
    }
}
