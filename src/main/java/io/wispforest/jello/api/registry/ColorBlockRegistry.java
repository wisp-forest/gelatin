package io.wispforest.jello.api.registry;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorBlockRegistry {

    private static final Logger LOGGER = LogManager.getLogger(ColorBlockRegistry.class);

    public static final Map<DyeableBlockVariant, BlockVariantEntrysContainer> REGISTRY = new HashMap<>();

    private static final List<DyeableBlockVariant> CURRENT_TYPES = new ArrayList<>();

    static {
        CURRENT_TYPES.addAll(VanillaBlockVariants.VANILLA_VARIANTS);
    }

    public static void registerBlockType(DyeableBlockVariant dyeableBlockVariant){
        for(DyeColorant dyeColorant : DyeColorantRegistry.DYE_COLOR) {
            DyedVariantContainer dyedVariants = DyedVariantContainer.DYED_VARIANTS.get(dyeColorant);

            if(dyedVariants != null) {
                BlockVariantEntrysContainer container = getOrCreateContainer(dyeableBlockVariant);

                container.addDyeBlockPair(dyeColorant, dyedVariants.dyedBlocks.get(dyeableBlockVariant));

                REGISTRY.put(dyeableBlockVariant, container);
            }
        }


        CURRENT_TYPES.add(dyeableBlockVariant);
    }

    public static void registerDyeColorant(DyeColorant dyeColorant){
        DyedVariantContainer dyedVariant = DyedVariantContainer.DYED_VARIANTS.get(dyeColorant);
        if(dyedVariant == null) {
            return;
        }

        registerDyeColorant(dyeColorant, dyedVariant);
    }

    public static void registerDyeColorant(DyeColorant dyeColorant, DyedVariantContainer dyedVariantContainer){
        for(DyeableBlockVariant blockVariant : CURRENT_TYPES) {
            BlockVariantEntrysContainer container = getOrCreateContainer(blockVariant);

            container.addDyeBlockPair(dyeColorant, dyedVariantContainer.dyedBlocks.get(blockVariant));

            REGISTRY.put(blockVariant, container);
        }
    }

    public static Block getVariant(Block block, DyeColorant color) {
        BlockVariantEntrysContainer container = null;

        for(Map.Entry<DyeableBlockVariant, BlockVariantEntrysContainer> entry : REGISTRY.entrySet()){
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

    public static BlockVariantEntrysContainer getOrCreateContainer(DyeableBlockVariant dyeableBlockVariant){
        if(REGISTRY.containsKey(dyeableBlockVariant)){
            return REGISTRY.get(dyeableBlockVariant);
        }else{
            return new BlockVariantEntrysContainer(dyeableBlockVariant.getPrimaryBlockTag(), Registry.BLOCK.get(dyeableBlockVariant.defaultBlock));
        }
    }

    public static class BlockVariantEntrysContainer {
        public final TagKey<Block> blockTag;

        public Map<DyeColorant, Block> coloredBlocks;

        public BlockVariantEntrysContainer(TagKey<Block> blockTag, Block defaultBlock){
            this.blockTag = blockTag;
            this.coloredBlocks = new HashMap<>();

            coloredBlocks.put(null, defaultBlock);
        }

        public void addDyeBlockPair(DyeColorant dyeColorant, Block block){
            this.coloredBlocks.put(dyeColorant, block);
        }


    }
}
