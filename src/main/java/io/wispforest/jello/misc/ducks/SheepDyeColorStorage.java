package io.wispforest.jello.misc.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;

public interface SheepDyeColorStorage {

    DyeColorant getWoolDyeColor();

    void setWoolDyeColor(DyeColorant dyeColorant);

    static float[] getDyedColor(DyeColorant dyeColorant) {
        if (dyeColorant == DyeColorantRegistry.WHITE) {
            return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
        } else {
            float[] fs = dyeColorant.getColorComponents();
            float f = 0.75F;
            return new float[]{fs[0] * 0.75F, fs[1] * 0.75F, fs[2] * 0.75F};
        }
    }
}
