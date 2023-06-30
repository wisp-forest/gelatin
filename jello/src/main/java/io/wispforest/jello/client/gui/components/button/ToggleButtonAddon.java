package io.wispforest.jello.client.gui.components.button;

import io.wispforest.owo.ui.base.BaseParentComponent;

import java.util.ArrayList;
import java.util.List;

public class ToggleButtonAddon<T extends BaseParentComponent> extends ButtonAddon<T> implements ButtonAddon.PressAction<T> {

    public List<ToggleButtonAddon<T>> connectedButtons = new ArrayList<>();

    public boolean selected = false;

    public ToggleButtonAddon(T component) {
        super(component);
    }

    @Override
    public void onPress(T button) {
        this.onPress.onPress(button);

        this.connectedButtons.forEach(b -> b.selected = false);

        this.selected = !this.selected;
    }
}
