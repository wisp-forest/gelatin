package io.wispforest.gelatin.dye_entities.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.gelatin.common.util.ColorUtil;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class GrayScaledTexture extends ResourceTexture {

    private final Identifier parentImageLocation;

    public GrayScaledTexture(Identifier grayScaleId, Identifier parentImageLocation) {
        super(grayScaleId);

        this.parentImageLocation = parentImageLocation;
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        TextureData textureData = this.loadTextureData(manager);
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

        NativeImage nativeImage = ColorUtil.convertImageToGrayScale(textureData.getImage());


        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.upload(nativeImage, bl, bl2));
        } else {
            this.upload(nativeImage, bl, bl2);
        }

    }

    @Override
    public TextureData loadTextureData(ResourceManager resourceManager) {
        return TextureData.load(resourceManager, this.parentImageLocation);
    }


}
