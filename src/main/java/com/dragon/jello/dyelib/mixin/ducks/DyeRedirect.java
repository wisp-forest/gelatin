package com.dragon.jello.dyelib.mixin.ducks;

import com.dragon.jello.dyelib.DyeColorRegistry;
import net.minecraft.item.DyeItem;

import static com.dragon.jello.dyelib.DyeColorRegistry.DYE_COLOR_TO_DYEITEM;

public interface DyeRedirect {

    DyeColorRegistry.DyeColor getDyeColor();

    static DyeItem byColor(DyeColorRegistry.DyeColor color) {
        return DYE_COLOR_TO_DYEITEM.get(color);
    }
}
