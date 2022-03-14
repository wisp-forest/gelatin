package io.wispforest.jello.api.mixin.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import net.minecraft.item.DyeItem;

public interface DyeRedirect {

    DyeColorant getDyeColor();

    static DyeItem byColor(DyeColorant color) {
        return DyeColorRegistry.DYE_COLOR_TO_DYEITEM.get(color);
    }
}
