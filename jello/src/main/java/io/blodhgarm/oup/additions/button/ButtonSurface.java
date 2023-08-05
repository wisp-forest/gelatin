package io.blodhgarm.oup.additions.button;

import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;

public interface ButtonSurface {
    void draw(ButtonAddon<?> buttonAddon, OwoUIDrawContext context, ParentComponent component);
}
