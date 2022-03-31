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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ColorBlockRegistry {

    private static final Logger LOGGER = LogManager.getLogger(ColorBlockRegistry.class);

    public static final Map<DyeableBlockVariant, BlockVariantEntriesContainer> REGISTRY = new HashMap<>();

    private static final Set<DyeableBlockVariant> CURRENT_TYPES = new HashSet<>();

    static {
        for (DyeableBlockVariant dyeableBlockVariant : VanillaBlockVariants.VANILLA_VARIANTS) {
            addToListWithRecursion(dyeableBlockVariant);
        }
    }

    public static void addToListWithRecursion(DyeableBlockVariant parentBlockVariant) {
        CURRENT_TYPES.add(parentBlockVariant);

        if (parentBlockVariant.childVariant != null) {
            addToListWithRecursion(parentBlockVariant.childVariant.get());
        }
    }

    public static void registerBlockTypeWithRecursion(DyeableBlockVariant parentBlockVariant) {
        CURRENT_TYPES.add(parentBlockVariant);

        for (DyeColorant dyeColorant : DyeColorantRegistry.DYE_COLOR) {
            DyedVariantContainer dyedVariants = DyedVariantContainer.getContainer(dyeColorant);

            if (dyedVariants != null) {
                BlockVariantEntriesContainer container = getOrCreateContainer(parentBlockVariant);

                container.addDyeBlockPair(dyeColorant, dyedVariants.dyedBlocks.get(parentBlockVariant));

                REGISTRY.put(parentBlockVariant, container);
            }
        }

        if (parentBlockVariant.childVariant != null) {
            registerBlockTypeWithRecursion(parentBlockVariant.childVariant.get());
        }

    }

    public static void registerDyeColorant(DyeColorant dyeColorant) {
        DyedVariantContainer dyedVariant = DyedVariantContainer.getContainer(dyeColorant);
        if (dyedVariant == null) {
            return;
        }

        registerDyeColorant(dyeColorant, dyedVariant);
    }

    public static void registerDyeColorant(DyeColorant dyeColorant, DyedVariantContainer dyedVariantContainer) {
        for (DyeableBlockVariant blockVariant : CURRENT_TYPES) {
            BlockVariantEntriesContainer container = getOrCreateContainer(blockVariant);

            container.addDyeBlockPair(dyeColorant, dyedVariantContainer.dyedBlocks.get(blockVariant));

            REGISTRY.put(blockVariant, container);
        }
    }

    public static Block getVariant(Block block, DyeColorant color) {
        BlockVariantEntriesContainer container = null;

        for (Map.Entry<DyeableBlockVariant, BlockVariantEntriesContainer> entry : REGISTRY.entrySet()) {
            if (block.getRegistryEntry().isIn(entry.getValue().blockTag)) {
                container = entry.getValue();
            }
        }

        if (container != null) {
            return container.coloredBlocks.get(color);
        } else {
            return null;
        }
    }

    //-----------------------------------------------------------------------//

    private static String getDyeColorFromBlock(Block block) {
        return getDyeColorFromIdentifier(Registry.BLOCK.getId(block));
    }

    private static String getDyeColorFromIdentifier(Identifier identifier) {
        String[] splitName = identifier.getPath().split("_");
        if (splitName.length >= 2) {
            return splitName[0].equals("light") ? splitName[0] + "_" + splitName[1] : splitName[0];
        } else {
            return "";
        }
    }

    //-----------------------------------------------------------------------//

    @ApiStatus.Internal
    private static void loggerError(String message) {
        LOGGER.error("[Color Block Registry] Error: " + message);
    }

    public static BlockVariantEntriesContainer getOrCreateContainer(DyeableBlockVariant dyeableBlockVariant) {
        if (REGISTRY.containsKey(dyeableBlockVariant)) {
            return REGISTRY.get(dyeableBlockVariant);
        } else {
            return new BlockVariantEntriesContainer(dyeableBlockVariant.getPrimaryBlockTag(), dyeableBlockVariant.getDefaultBlockVariant());
        }
    }

    public static class BlockVariantEntriesContainer {
        public final TagKey<Block> blockTag;

        public Map<DyeColorant, Block> coloredBlocks;

        public BlockVariantEntriesContainer(TagKey<Block> blockTag, Block defaultBlock) {
            this.blockTag = blockTag;
            this.coloredBlocks = new HashMap<>();

            coloredBlocks.put(null, defaultBlock);
        }

        public void addDyeBlockPair(DyeColorant dyeColorant, Block block) {
            this.coloredBlocks.put(dyeColorant, block);
        }


    }
}
