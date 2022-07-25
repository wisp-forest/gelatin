package io.wispforest.jello.client;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.jello.api.util.ColorUtil;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.IntBuffer;

public class GrayScaledTexture extends ResourceTexture {

    private final Identifier parentImageLocation;

    public GrayScaledTexture(Identifier grayScaleId, Identifier parentImageLocation) {
        super(grayScaleId);

        this.parentImageLocation = parentImageLocation;
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        ResourceTexture.TextureData textureData = this.loadTextureData(manager);
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

        NativeImage nativeImage = convertImageToGrayScale(textureData.getImage());


        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.upload(nativeImage, bl, bl2));
        } else {
            this.upload(nativeImage, bl, bl2);
        }

    }

    @Override
    public ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
        return ResourceTexture.TextureData.load(resourceManager, this.parentImageLocation);
    }

    public static NativeImage convertImageToGrayScale(NativeImage nativeImageOrigin){
        NativeImage imageCopy = new NativeImage(nativeImageOrigin.getWidth(), nativeImageOrigin.getHeight(),false);
        imageCopy.copyFrom(nativeImageOrigin);

        long pointer = imageCopy.pointer;

        final IntBuffer buffer = MemoryUtil.memIntBuffer(pointer, (imageCopy.getWidth() * imageCopy.getHeight()));

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

                //int grayValue = (r1 + g1 + b1) / 3;

                int grayValue = ColorUtil.toGray(ColorUtil.luminance(r1, g1, b1));

                pixelColors[index] = ((a1 & 0xFF) << 24) | ((grayValue & 0xFF) << 16) | ((grayValue & 0xFF) << 8)  | ((grayValue & 0xFF) << 0);
            }
        }

        buffer.put(pixelColors);
        buffer.clear();

        return imageCopy;
    }
}
