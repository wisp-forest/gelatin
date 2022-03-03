package com.dragon.jello.api.dye.item;

import com.dragon.jello.api.dye.registry.DyeColorRegistry;
import com.dragon.jello.api.dye.DyeColorant;
import com.dragon.jello.api.mixin.ducks.DyeRedirect;

public class DyeItem extends net.minecraft.item.DyeItem implements DyeRedirect {
    protected final DyeColorant mainColor;

    public DyeItem(DyeColorant mainColor, Settings settings) {
        super(DyeColorRegistry.NULL_VALUE_OLD, settings);

        this.mainColor = mainColor;

        if(mainColor != null){
            DyeColorRegistry.DYE_COLOR_TO_DYEITEM.put(this.mainColor, this);
        }
    }

    @Override
    public DyeColorant getDyeColor() {
        return this.mainColor;
    }
}
