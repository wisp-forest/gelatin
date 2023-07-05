package io.wispforest.jello.StorageInventoryScreens;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.jello.Jello;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.util.Drawer;
import net.minecraft.client.util.math.MatrixStack;

public class StorageBackgroundComponent extends TextureComponent {

    private int slotWidth, slotHeight;

    private boolean scrollBarIncluded, showTopText;

    protected StorageBackgroundComponent(int regionWidth, int regionHeight, int slotWidth, int slotHeight, boolean scrollBarIncluded, boolean showTopText) {
        super(Jello.id("textures/gui/storage_background.png"), 0, 0, regionWidth, regionHeight, 64, 64);

        this.slotWidth = slotWidth;
        this.slotHeight = slotHeight;

        this.scrollBarIncluded = scrollBarIncluded;
        this.showTopText = showTopText;
    }

    public static StorageBackgroundComponent of(int slotWidth, int slotHeight, boolean scrollBarIncluded, boolean showTopText){
        int regionWidth = 7 + (slotWidth * 18) + (scrollBarIncluded ? 11 : 0) + 7;
        int regionHeight = 7 + (slotHeight * 18) + (showTopText ? 10 : 0) + 7;

        return new StorageBackgroundComponent(regionWidth, regionHeight, slotWidth, slotHeight, scrollBarIncluded, showTopText);
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        RenderSystem.setShaderTexture(0, this.texture);

        drawOutline(matrices, mouseX, mouseY, partialTicks, delta);
        drawSlots(matrices, mouseX, mouseY, partialTicks, delta);

        if(scrollBarIncluded) {
            int scrollBarX = x + (regionWidth - 18);
            int scrollBarY = y + (showTopText ? 17 : 7);

            Drawer.drawTexture(matrices, scrollBarX, scrollBarY, 25, 17, 11, 6, this.textureWidth, this.textureHeight);

            Drawer.drawTexture(matrices, scrollBarX, scrollBarY + 6, 11, ((slotHeight * 18) - 12), 25, 23, 11, 6, this.textureWidth, this.textureHeight);

            Drawer.drawTexture(matrices, scrollBarX, scrollBarY + (slotHeight * 18) - 6, 25, 29, 11, 6, this.textureWidth, this.textureHeight);
        }
    }

    public void drawOutline(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta){
        int x = this.x;
        int y = this.y;

        //Top
        int topHeight = (showTopText ? 17 : 7);

        Drawer.drawTexture(matrices, x, y, 7, topHeight, 0, 0, 7, topHeight, this.textureWidth, this.textureHeight);
        Drawer.drawTexture(matrices, x + 7, y, (this.regionWidth - 14), topHeight, 7, 0, 18, topHeight, this.textureWidth, this.textureHeight);
        Drawer.drawTexture(matrices, x + regionWidth - 7, y, 7, topHeight, 36, 0, 7, topHeight, this.textureWidth, this.textureHeight);

        //Bottom
        int bottomY = y + regionHeight - 7;

        Drawer.drawTexture(matrices, x, bottomY, 7, 17, 0, 35, 7, 17, this.textureWidth, this.textureHeight);
        Drawer.drawTexture(matrices, x + 7, bottomY, (this.regionWidth - 14), 17, 7, 35, 18,17, this.textureWidth, this.textureHeight);
        Drawer.drawTexture(matrices, x + regionWidth - 7, bottomY, 7, 17, 36, 35, 7, 17, this.textureWidth, this.textureHeight);

        //Left
        Drawer.drawTexture(matrices, x, y + topHeight,  7, (this.regionHeight - (topHeight + 7)), 0, 17, 7, 18, this.textureWidth, this.textureHeight);

        //Right
        Drawer.drawTexture(matrices, x + regionWidth - 7, y + topHeight, 7, (this.regionHeight - (topHeight + 7)), 36, 7, 7, 18, this.textureWidth, this.textureHeight);
    }

    public void drawSlots(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta){
        int textureX;
        int textureY = this.y + (showTopText ?  17 : 7);

        for(int slotY = 0; slotY < slotHeight; slotY++){
            textureX = this.x + 7;

            for(int slotX = 0; slotX < slotWidth; slotX++){
                Drawer.drawTexture(matrices, textureX, textureY, 7, 17, 18, 18, this.textureWidth, this.textureHeight);

                textureX = textureX + 18;
            }

            textureY = textureY + 18;
        }
    }
}
