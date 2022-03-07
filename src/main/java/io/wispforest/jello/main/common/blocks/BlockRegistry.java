package io.wispforest.jello.main.common.blocks;

import io.wispforest.jello.main.common.items.MultiColorBlockItem;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.List;

public class BlockRegistry {

    public static class SlimeBlockRegistry implements BlockRegistryContainer{
        public static final Block WHITE_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.WHITE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block ORANGE_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.ORANGE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block MAGENTA_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.MAGENTA, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block LIGHT_BLUE_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.LIGHT_BLUE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block YELLOW_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.YELLOW, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block LIME_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.LIME, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block PINK_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.PINK, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block GRAY_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.GRAY, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block LIGHT_GRAY_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.LIGHT_GRAY, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block CYAN_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.CYAN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block PURPLE_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.PURPLE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block BLUE_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.BLUE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block BROWN_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.BROWN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block GREEN_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.GREEN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block RED_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.RED, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block BLACK_SLIME_BLOCK = new SlimeBlockColored(DyeColorRegistry.BLACK, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));

        public static final List<Block> COLORED_SLIME_BLOCKS = List
                .of(WHITE_SLIME_BLOCK, ORANGE_SLIME_BLOCK, MAGENTA_SLIME_BLOCK, LIGHT_BLUE_SLIME_BLOCK,
                        YELLOW_SLIME_BLOCK, LIME_SLIME_BLOCK, PINK_SLIME_BLOCK, GRAY_SLIME_BLOCK,
                        LIGHT_GRAY_SLIME_BLOCK, CYAN_SLIME_BLOCK, PURPLE_SLIME_BLOCK, BLUE_SLIME_BLOCK,
                        BROWN_SLIME_BLOCK, GREEN_SLIME_BLOCK, RED_SLIME_BLOCK, BLACK_SLIME_BLOCK);

        @Override
        public BlockItem createBlockItem(Block block, String identifier) {
            if(block instanceof DyeableBlock){
                return new MultiColorBlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE));
            }else{
                return new BlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE));
            }
        }
    }

    public static class SlimeSlabRegistry implements BlockRegistryContainer{
        public static final Block SLIME_SLAB = new SlimeSlab(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));

        public static final Block WHITE_SLIME_SLAB= new SlimeSlabColored(DyeColorRegistry.WHITE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block ORANGE_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.ORANGE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block MAGENTA_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.MAGENTA, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block LIGHT_BLUE_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.LIGHT_BLUE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block YELLOW_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.YELLOW, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block LIME_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.LIME, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block PINK_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.PINK, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block GRAY_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.GRAY, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block LIGHT_GRAY_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.LIGHT_GRAY, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block CYAN_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.CYAN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block PURPLE_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.PURPLE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block BLUE_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.BLUE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block BROWN_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.BROWN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block GREEN_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.GREEN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block RED_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.RED, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
        public static final Block BLACK_SLIME_SLAB = new SlimeSlabColored(DyeColorRegistry.BLACK, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));

        public static final List<Block> COLORED_SLIME_SLABS = List
                .of(WHITE_SLIME_SLAB, ORANGE_SLIME_SLAB, MAGENTA_SLIME_SLAB, LIGHT_BLUE_SLIME_SLAB,
                        YELLOW_SLIME_SLAB, LIME_SLIME_SLAB, PINK_SLIME_SLAB, GRAY_SLIME_SLAB,
                        LIGHT_GRAY_SLIME_SLAB, CYAN_SLIME_SLAB, PURPLE_SLIME_SLAB, BLUE_SLIME_SLAB,
                        BROWN_SLIME_SLAB, GREEN_SLIME_SLAB, RED_SLIME_SLAB, BLACK_SLIME_SLAB);

        @Override
        public BlockItem createBlockItem(Block block, String identifier) {
            if(block instanceof DyeableBlock){
                return new MultiColorBlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE));
            }else{
                return new BlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE));
            }
        }
    }

    public static class MainBlockRegistry implements BlockRegistryContainer{

        //@NoBlockItem
        //public static final Block WATER_CAULDRON = new DyeableCauldronBlock(FabricBlockSettings.copy(Blocks.CAULDRON), LeveledCauldronBlock.RAIN_PREDICATE, JelloCauldronBehaviors.DYE_WATER_CAULDRON_BEHAVIOR);
    }
}
