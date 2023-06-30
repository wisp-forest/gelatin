package io.wispforest.jello.client.gui.components;

import net.minecraft.util.ClickType;

public interface ComponentClickedEvent {
    boolean onClicked(SelectableItemComponent component, ClickType type, double mouseX, double mouseY);
}
