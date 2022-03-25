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

public class BlockRegistry {

    public static class SlimeBlockRegistry implements BlockRegistryContainer{
//        public static final Block WHITE_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.WHITE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block ORANGE_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.ORANGE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block MAGENTA_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.MAGENTA, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block LIGHT_BLUE_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.LIGHT_BLUE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block YELLOW_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.YELLOW, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block LIME_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.LIME, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block PINK_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.PINK, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block GRAY_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.GRAY, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block LIGHT_GRAY_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.LIGHT_GRAY, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block CYAN_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.CYAN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block PURPLE_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.PURPLE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block BLUE_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.BLUE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block BROWN_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.BROWN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block GREEN_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.GREEN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block RED_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.RED, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block BLACK_SLIME_BLOCK = new SlimeBlockColored(DyeColorantRegistry.BLACK, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//
//        public static final List<Block> COLORED_SLIME_BLOCKS = List
//                .of(WHITE_SLIME_BLOCK, ORANGE_SLIME_BLOCK, MAGENTA_SLIME_BLOCK, LIGHT_BLUE_SLIME_BLOCK,
//                        YELLOW_SLIME_BLOCK, LIME_SLIME_BLOCK, PINK_SLIME_BLOCK, GRAY_SLIME_BLOCK,
//                        LIGHT_GRAY_SLIME_BLOCK, CYAN_SLIME_BLOCK, PURPLE_SLIME_BLOCK, BLUE_SLIME_BLOCK,
//                        BROWN_SLIME_BLOCK, GREEN_SLIME_BLOCK, RED_SLIME_BLOCK, BLACK_SLIME_BLOCK);

        @Override
        public BlockItem createBlockItem(Block block, String identifier) {
            if(block instanceof DyeBlockStorage dyeBlockStorage && dyeBlockStorage.getDyeColor() != DyeColorantRegistry.NULL_VALUE_NEW){
                return new ColoredBlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE));
            }else{
                return new BlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE));
            }
        }
    }

    public static class SlimeSlabRegistry implements BlockRegistryContainer{
        public static final Block SLIME_SLAB = new SlimeSlab(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));

//        public static final Block WHITE_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.WHITE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block ORANGE_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.ORANGE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block MAGENTA_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.MAGENTA, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block LIGHT_BLUE_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.LIGHT_BLUE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block YELLOW_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.YELLOW, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block LIME_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.LIME, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block PINK_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.PINK, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block GRAY_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.GRAY, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block LIGHT_GRAY_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.LIGHT_GRAY, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block CYAN_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.CYAN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block PURPLE_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.PURPLE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block BLUE_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.BLUE, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block BROWN_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.BROWN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block GREEN_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.GREEN, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block RED_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.RED, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//        public static final Block BLACK_SLIME_SLAB = new SlimeSlabColored(DyeColorantRegistry.BLACK, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
//
//        public static final List<Block> COLORED_SLIME_SLABS = List
//                .of(WHITE_SLIME_SLAB, ORANGE_SLIME_SLAB, MAGENTA_SLIME_SLAB, LIGHT_BLUE_SLIME_SLAB,
//                        YELLOW_SLIME_SLAB, LIME_SLIME_SLAB, PINK_SLIME_SLAB, GRAY_SLIME_SLAB,
//                        LIGHT_GRAY_SLIME_SLAB, CYAN_SLIME_SLAB, PURPLE_SLIME_SLAB, BLUE_SLIME_SLAB,
//                        BROWN_SLIME_SLAB, GREEN_SLIME_SLAB, RED_SLIME_SLAB, BLACK_SLIME_SLAB);

        @Override
        public BlockItem createBlockItem(Block block, String identifier) {
            if(block instanceof DyeBlockStorage dyeBlockStorage && dyeBlockStorage.getDyeColor() != DyeColorantRegistry.NULL_VALUE_NEW){
                return new ColoredBlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE));
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
