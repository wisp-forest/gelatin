package io.wispforest.jello.client.gui.components.button;

import io.wispforest.owo.ui.core.ParentComponent;
import net.minecraft.client.util.math.MatrixStack;

public interface ButtonSurface {
    void draw(ButtonAddon<?> buttonAddon, MatrixStack matrices, ParentComponent component);
}
