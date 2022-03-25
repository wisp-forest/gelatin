package io.wispforest.jello.api.registry;

import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyedVariants;
import io.wispforest.jello.api.dye.registry.builder.BaseBlockBuilder;
import io.wispforest.jello.api.dye.registry.builder.BlockType;
import io.wispforest.jello.api.dye.registry.builder.VanillaBlockBuilder;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class ColorBlockRegistry {

    private static final Logger LOGGER = LogManager.getLogger(ColorBlockRegistry.class);

    public static final Map<BlockType, BlockTypeEntrysContainer> REGISTRY = new HashMap<>();

    private static final List<BlockType> CURRENT_TYPES = new ArrayList<>();

    static {
        for(VanillaBlockBuilder blockBuilder : VanillaBlockBuilder.VANILLA_BUILDERS){
            CURRENT_TYPES.addAll(blockBuilder.getBlockTypes());
        }
    }

    public static void registerBlockType(List<BlockType> blockTypes){
        CURRENT_TYPES.addAll(blockTypes);

        for(BlockType blockType : blockTypes) {
            for(DyeColorant dyeColorant : DyeColorantRegistry.DYE_COLOR) {
                DyedVariants dyedVariants = DyedVariants.DYED_VARIANTS.get(dyeColorant);

                if(dyedVariants != null) {
                    BlockTypeEntrysContainer container = getOrCreateContainer(blockType);

                    container.addDyeBlockPair(dyeColorant, dyedVariants.dyedBlocks.get(blockType));

                    REGISTRY.put(blockType, container);
                }
            }
        }
    }

    public static void registerDyeColorant(DyeColorant dyeColorant){
        DyedVariants dyedVariants = DyedVariants.DYED_VARIANTS.get(dyeColorant);
        if(dyedVariants == null) {
            return;
        }

        registerDyeColorant(dyeColorant, dyedVariants);
    }

    public static void registerDyeColorant(DyeColorant dyeColorant, DyedVariants dyedVariants){

        for(BlockType blockType : CURRENT_TYPES) {
            BlockTypeEntrysContainer container = getOrCreateContainer(blockType);

            container.addDyeBlockPair(dyeColorant, dyedVariants.dyedBlocks.get(blockType));

            REGISTRY.put(blockType, container);
        }
    }

    public static Block getVariant(Block block, DyeColorant color) {
        BlockTypeEntrysContainer container = null;

        for(Map.Entry<BlockType, BlockTypeEntrysContainer> entry : REGISTRY.entrySet()){
            if(block.getRegistryEntry().isIn(entry.getValue().blockTag)){
                container = entry.getValue();
            }
        }

        if(container != null){
            return container.coloredBlocks.get(color);
        }else{
            return null;
        }
    }

    //-----------------------------------------------------------------------//

    private static String getDyeColorFromBlock(Block block){
        return getDyeColorFromIdentifier(Registry.BLOCK.getId(block));
    }

    private static String getDyeColorFromIdentifier(Identifier identifier) {
        String[] splitName = identifier.getPath().split("_");
        if (splitName.length >= 2) {
            return splitName[0].equals("light")  ? splitName[0] + "_" + splitName[1] : splitName[0];
        } else {
            return "";
        }
    }

    //-----------------------------------------------------------------------//

    @ApiStatus.Internal
    private static void loggerError(String message){
        LOGGER.error("[Color Block Registry] Error: " + message);
    }

    public static BlockTypeEntrysContainer getOrCreateContainer(BlockType blockType){
        if(REGISTRY.containsKey(blockType)){
            return REGISTRY.get(blockType);
        }else{
            return new BlockTypeEntrysContainer(blockType.blockTag, Registry.BLOCK.get(blockType.defaultBlockID));
        }
    }

    public static class BlockTypeEntrysContainer{
        public final TagKey<Block> blockTag;

        public Map<DyeColorant, Block> coloredBlocks;

        public BlockTypeEntrysContainer(TagKey<Block> blockTag, Block defaultBlock){
            this.blockTag = blockTag;
            this.coloredBlocks = new HashMap<>();

            coloredBlocks.put(null, defaultBlock);
        }

        public void addDyeBlockPair(DyeColorant dyeColorant, Block block){
            this.coloredBlocks.put(dyeColorant, block);
        }


    }
}
