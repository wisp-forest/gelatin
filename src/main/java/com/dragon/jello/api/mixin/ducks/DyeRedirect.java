package com.dragon.jello.api.mixin.ducks;

import com.dragon.jello.api.dye.DyeColorant;
import net.minecraft.item.DyeItem;

import static com.dragon.jello.api.dye.registry.DyeColorRegistry.DYE_COLOR_TO_DYEITEM;

public interface DyeRedirect {

    DyeColorant getDyeColor();

    static DyeItem byColor(DyeColorant color) {
        return DYE_COLOR_TO_DYEITEM.get(color);
    }
}
