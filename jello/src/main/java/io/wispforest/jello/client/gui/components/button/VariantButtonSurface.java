package io.wispforest.jello.client.gui.components.button;

import io.wispforest.jello.Jello;
import io.wispforest.jello.client.gui.VariantsNinePatchRender;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Size;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class VariantButtonSurface extends VariantsNinePatchRender implements ButtonSurface {

    public VariantButtonSurface(Identifier texture, Size patchSize, Size textureSize, boolean repeat) {
        super(texture, patchSize, textureSize, repeat);
    }

    public static VariantButtonSurface surfaceLike(Size patchSize, Size textureSize, boolean repeat, boolean darkMode){
        return surfaceLike(patchSize, textureSize, repeat, darkMode, false);
    }

    public static VariantButtonSurface surfaceLike(Size patchSize, Size textureSize, boolean repeat, boolean darkMode, boolean squareVariant){
        return (VariantButtonSurface) new VariantButtonSurface(Jello.id("textures/gui/button_surface.png"), patchSize, textureSize, repeat)
                .setVIndex(ButtonAddon.getVIndex(darkMode, squareVariant));
    }

    @Override
    public void draw(ButtonAddon<?> buttonAddon, MatrixStack matrices, ParentComponent component) {
        this.setUIndex(buttonAddon.isActive() ? (buttonAddon.isHovered() ? 1 : 0) : 2);

        this.draw(matrices, component);
    }
}
