package io.wispforest.jello.misc.debug;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.block.Block;

public record ColorDataStorage(DyeColorant colorant, Color color, Block block) {
}
