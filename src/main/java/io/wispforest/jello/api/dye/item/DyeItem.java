package io.wispforest.jello.api.dye.item;

import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.mixin.ducks.DyeRedirect;

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
