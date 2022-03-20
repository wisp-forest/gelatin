package io.wispforest.jello.api.mixin.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import net.minecraft.item.DyeItem;

public interface DyeItemStorage {

    DyeColorant getDyeColor();

}
