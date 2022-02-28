package com.dragon.jello.mixin.ducks;

import com.dragon.jello.lib.dyecolor.DyeColorRegistry;
import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;

import static com.dragon.jello.lib.dyecolor.DyeColorRegistry.DYE_COLOR_TO_DYEITEM;

public interface DyeItemRedirect {

    DyeColorRegistry.DyeColor getDyeColor();

    static DyeItem byColor(DyeColorRegistry.DyeColor color) {
        return DYE_COLOR_TO_DYEITEM.get(color);
    }
}
