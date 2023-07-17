package io.wispforest.jello.block;

import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class JelloBlocks implements BlockRegistryContainer {

    public static final Block SLIME_SLAB = new SlimeSlab(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
    public static final Block PAINT_MIXER = new ColorMixerBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque());

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new Item.Settings()/*.group(ItemGroup.REDSTONE)*/);
    }

}


