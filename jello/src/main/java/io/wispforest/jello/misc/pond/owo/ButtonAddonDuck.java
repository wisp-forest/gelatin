package io.wispforest.jello.misc.pond.owo;

import io.blodhgarm.oup.additions.button.ButtonAddon;
import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.container.FlowLayout;

import java.util.function.Function;

public interface ButtonAddonDuck<T extends BaseParentComponent> {

    T setButtonAddon(Function<FlowLayout, ButtonAddon<FlowLayout>> addonBuilder);

    ButtonAddon<T> getAddon();
}
