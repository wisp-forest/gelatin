package io.blodhgarm.oup.additions.button;

import io.blodhgarm.oup.utils.VariantsNinePatchRender;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Size;
import net.minecraft.util.Identifier;

public class VariantButtonSurface extends VariantsNinePatchRender implements ButtonSurface {

    public VariantButtonSurface(Identifier texture, Size patchSize, Size textureSize, boolean repeat) {
        super(texture, patchSize, textureSize, repeat);
    }

    public static VariantButtonSurface surfaceLike(Size patchSize, Size textureSize, boolean repeat, boolean darkMode){
        return surfaceLike(patchSize, textureSize, repeat, darkMode, false);
    }

    public static VariantButtonSurface surfaceLike(Size patchSize, Size textureSize, boolean repeat, boolean darkMode, boolean squareVariant){
        return (VariantButtonSurface) new VariantButtonSurface(new Identifier("oup","textures/gui/button_surface.png"), patchSize, textureSize, repeat)
                .setVIndex(ButtonAddon.getVIndex(darkMode, squareVariant));
    }

    @Override
    public void draw(ButtonAddon<?> buttonAddon, OwoUIDrawContext context, ParentComponent component) {
        this.setUIndex(buttonAddon.isActive() ? (buttonAddon.isHovered() ? 1 : 0) : 2);

        this.draw(context, component);
    }
}
