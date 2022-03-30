package io.wispforest.jello.main.common.blocks;

import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import io.wispforest.jello.api.dye.item.ColoredBlockItem;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.List;

public class JelloBlockRegistry implements BlockRegistryContainer {

    public static final Block SLIME_SLAB = new SlimeSlab(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
    public static final Block COLOR_MIXER = new ColorMixerBlock(FabricBlockSettings.copyOf(Blocks.ANVIL));


    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE));
    }



}
