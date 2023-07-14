package io.wispforest.gelatin.dye_registry.mixins;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements DyeBlockStorage {

    public BlockMixin(Settings settings) {
        super(settings);
    }

    private DyeColorant blockDyeColor = DyeColorantRegistry.NULL_VALUE_NEW;

    @Override
    public DyeColorant getDyeColorant() {
        return blockDyeColor;
    }

    @Override
    public void setDyeColor(DyeColorant dyeColorant) {
        this.blockDyeColor = dyeColorant;
    }
}
