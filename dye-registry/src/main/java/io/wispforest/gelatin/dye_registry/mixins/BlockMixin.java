package io.wispforest.gelatin.dye_registry.mixins;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.util.registry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements DyeStorage {

    public BlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    @Deprecated
    public abstract RegistryEntry.Reference<Block> getRegistryEntry();

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
