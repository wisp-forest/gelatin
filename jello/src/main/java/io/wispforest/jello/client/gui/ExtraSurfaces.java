package io.wispforest.jello.client.gui;

import io.wispforest.owo.ui.core.Surface;
import net.minecraft.util.Identifier;

public class ExtraSurfaces {

    public static Identifier INVERSE_PANEL_TEXTURE = new Identifier("jello", "textures/gui/inverse_panel.png");

    public static Surface INVERSE_PANEL = (context, component) -> {
        int x = component.x();
        int y = component.y();
        int width = component.width();
        int height = component.height();

        context.drawTexture(INVERSE_PANEL_TEXTURE, x, y, 0, 0, 5, 5, 16, 16);
        context.drawTexture(INVERSE_PANEL_TEXTURE, x + width - 5, y, 10, 0, 5, 5, 16, 16);
        context.drawTexture(INVERSE_PANEL_TEXTURE, x, y + height - 5, 0, 10, 5, 5, 16, 16);
        context.drawTexture(INVERSE_PANEL_TEXTURE, x + width - 5, y + height - 5, 10, 10, 5, 5, 16, 16);

        if (width > 10 && height > 10) {
            context.drawTexture(INVERSE_PANEL_TEXTURE,x + 5, y + 5, width - 10, height - 10, 5, 5, 5, 5, 16, 16);
        }

        if (width > 10) {
            context.drawTexture(INVERSE_PANEL_TEXTURE,x + 5, y, width - 10, 5, 5, 0, 5, 5, 16, 16);
            context.drawTexture(INVERSE_PANEL_TEXTURE,x + 5, y + height - 5, width - 10, 5, 5, 10, 5, 5, 16, 16);
        }

        if (height > 10) {
            context.drawTexture(INVERSE_PANEL_TEXTURE, x, y + 5, 5, height - 10, 0, 5, 5, 5, 16, 16);
            context.drawTexture(INVERSE_PANEL_TEXTURE,x + width - 5, y + 5, 5, height - 10, 10, 5, 5, 5, 16, 16);
        }
    };
}
