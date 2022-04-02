package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.data.tags.JelloTags;
import io.wispforest.jello.item.JelloDyeItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DyedVariantContainer {

    protected static final Map<DyeColorant, DyedVariantContainer> DYED_VARIANTS = new HashMap<>();

    public Map<DyeableBlockVariant, Block> dyedBlocks;
    public DyeItem dyeItem;

    public BlockBuilder blockBuilder;
    public ItemBuilder itemBuilder;

    protected DyedVariantContainer(Map<DyeableBlockVariant, Block> dyedBlocks, DyeItem dyeItem, BlockBuilder blockBuilder, ItemBuilder itemBuilder) {
        this.dyedBlocks = dyedBlocks;
        this.dyeItem = dyeItem;
        this.blockBuilder = blockBuilder;
        this.itemBuilder = itemBuilder;
    }

    /**
     * Get the Block Based on the given {@link DyeColorant} and {@link DyeableBlockVariant}
     *
     * @param dyeColorant         The Color of the block to be searched for
     * @param dyeableBlockVariant The Block Variant the block is of
     * @return either the block found or {@link Blocks#AIR} if no block was found
     */
    public static Block getDyedBlockVariant(@Nonnull DyeColorant dyeColorant, @Nonnull DyeableBlockVariant dyeableBlockVariant) {
        for (Map.Entry<DyeColorant, DyedVariantContainer> variantContainerEntry : DYED_VARIANTS.entrySet()) {
            DyeColorant possibleDyeColorant = variantContainerEntry.getKey();

            if (dyeColorant == possibleDyeColorant) {
                DyedVariantContainer container = variantContainerEntry.getValue();

                for (Map.Entry<DyeableBlockVariant, Block> variantBlockEntry : container.dyedBlocks.entrySet()) {
                    DyeableBlockVariant possibleVariant = variantBlockEntry.getKey();

                    if (dyeableBlockVariant == possibleVariant) {
                        return variantBlockEntry.getValue();
                    }
                }
            }
        }

        return Blocks.AIR;
    }

    /**
     * Attempts to get the Container if the {@link DyeColorant} has a registered Variant
     *
     * @param dyeColorant The Color used to search the Map
     * @return {@link DyedVariantContainer} if found or null by default
     */
    @Nullable
    public static DyedVariantContainer getContainer(DyeColorant dyeColorant) {
        return DYED_VARIANTS.get(dyeColorant);
    }

    /**
     * Way of accessing the Variant Map without Direct access
     *
     * @return the current Map of all {@link DyedVariantContainer} made
     */
    public static Map<DyeColorant, DyedVariantContainer> getVariantMap() {
        return DYED_VARIANTS;
    }

    //-----------------------------------------------------------------------------------------------------

    @ApiStatus.Internal
    public static DyedVariantContainer createVariantContainer(DyeColorant dyeColorant) {
        return createVariantContainer(dyeColorant, null, null, true, false);
    }

    public static DyedVariantContainer createVariantContainer(DyeColorant dyeColorant, Item.Settings dyeItemSettings, boolean useModelRedirectSystem) {
        return createVariantContainer(dyeColorant, dyeItemSettings, null, false, useModelRedirectSystem);
    }

    public static DyedVariantContainer createVariantContainer(DyeColorant dyeColorant, Item.Settings dyeItemSettings, OwoItemSettings owoItemSettings, boolean readOnly, boolean useModelRedirectSystem) {
        BlockBuilder blockBuilder = new BlockBuilder(readOnly, useModelRedirectSystem);
        ItemBuilder itemBuilder = new ItemBuilder(readOnly, useModelRedirectSystem);

        Map<DyeableBlockVariant, Block> dyedBlocks = blockBuilder.buildVariantMapFromDye(dyeColorant, owoItemSettings);
        DyeItem dyeItem = itemBuilder.createDyeItem(dyeColorant, dyeItemSettings);

        DyedVariantContainer dyedVariantContainer = new DyedVariantContainer(dyedBlocks, dyeItem, blockBuilder, itemBuilder);

        DYED_VARIANTS.put(dyeColorant, dyedVariantContainer);

        ColorBlockRegistry.registerDyeColorant(dyeColorant, dyedVariantContainer);

        return dyedVariantContainer;
    }

    //-----------------------------------------------------------------------------------------------------

    //TODO: FILTER THE ENTRIES TO BE IN A CERTAIN COLOR ORDERED???
    protected static void updateExistingContainers(DyeableBlockVariant dyeableBlockVariant) {
        for (Map.Entry<DyeColorant, DyedVariantContainer> entry : DYED_VARIANTS.entrySet()) {
            DyeColorant dyeColorant = entry.getKey();

            entry.getValue().addToExistingContainerWithRecursion(dyeColorant, dyeableBlockVariant);
        }
    }

    private void addToExistingContainerWithRecursion(DyeColorant dyeColorant, DyeableBlockVariant dyeableBlockVariant) {
        this.blockBuilder.recursivelyBuildBlocksFromVariant(this.dyedBlocks, null, dyeableBlockVariant, dyeColorant, null);
    }

    public static class BlockBuilder {
        public boolean readOnly;
        public final boolean useModelRedirectSystem;

        public BlockBuilder(boolean readOnly, boolean useModelRedirectSystem) {
            this.readOnly = readOnly;
            this.useModelRedirectSystem = useModelRedirectSystem;
        }

        protected Map<DyeableBlockVariant, Block> buildVariantMapFromDye(DyeColorant dyeColorant, @Nullable OwoItemSettings overrideSettings) {
            Map<DyeableBlockVariant, Block> dyedBlocks = new HashMap<>();

            for (DyeableBlockVariant dyeableBlockVariant : VanillaBlockVariants.VANILLA_VARIANTS) {
                this.recursivelyBuildBlocksFromVariant(dyedBlocks, null, dyeableBlockVariant, dyeColorant, overrideSettings);
            }

            this.readOnly = false;

            return dyedBlocks;
        }

        private void recursivelyBuildBlocksFromVariant(Map<DyeableBlockVariant, Block> dyedBlocks, Block possibleParentBlock, DyeableBlockVariant parentBlockVariant, DyeColorant dyeColorant, @Nullable OwoItemSettings overrideSettings) {
            DyeableBlockVariant.RegistryInfo info = null;
            Block childBlock;

            if (!readOnly) {
                info = parentBlockVariant.makeChildBlock(dyeColorant, possibleParentBlock);

                if (overrideSettings != null)
                    info.setOverrideSettings(overrideSettings);

                childBlock = registerBlock(parentBlockVariant, info, dyeColorant);

                parentBlockVariant.addToBlockTags(childBlock);

                if(!info.noBlockItem()) {
                    parentBlockVariant.addToItemTags(childBlock.asItem(), false);
                }
            } else {
                childBlock = registerBlock(parentBlockVariant, null, dyeColorant);

                parentBlockVariant.addToBlockTags(childBlock, true);
                parentBlockVariant.addToItemTags(childBlock.asItem(), true);
            }

            dyedBlocks.put(parentBlockVariant, childBlock);

            if (parentBlockVariant.childVariant != null) {
                DyeableBlockVariant childBlockVariant = parentBlockVariant.childVariant.get();

                recursivelyBuildBlocksFromVariant(dyedBlocks, childBlock, childBlockVariant, dyeColorant, overrideSettings);
            }
        }

        private Block registerBlock(DyeableBlockVariant dyeableBlockVariant, @Nullable DyeableBlockVariant.RegistryInfo registryInfo, DyeColorant dyeColorant) {
            if (readOnly && Objects.equals(dyeColorant.getId().getNamespace(), "minecraft")) {
                return dyeableBlockVariant.getBlockVariant(dyeColorant);
            }

            String nameSpace = Objects.equals(dyeableBlockVariant.variantIdentifier.getNamespace(), "minecraft") ?
                    dyeColorant.getId().getNamespace() :
                    dyeableBlockVariant.variantIdentifier.getNamespace();

            Identifier identifier = new Identifier(nameSpace, dyeableBlockVariant.getBlockVariantPath(dyeColorant));

            if (this.useModelRedirectSystem) {
                DyeColorantRegistry.IDENTIFIER_RESOURCE_REDIRECTS.add(identifier);
            }

            Block block = Registry.register(Registry.BLOCK, identifier, registryInfo.block);

            if (!registryInfo.noBlockItem()) {
                Registry.register(Registry.ITEM, identifier, dyeableBlockVariant.makeBlockItem(dyeColorant, block, registryInfo.getItemSettings()));
            }

            return block;
        }
    }

    public static class ItemBuilder {
        public boolean readOnly;
        public final boolean useModelRedirectSystem;

        public ItemBuilder(boolean readOnly, boolean useModelRedirectSystem) {
            this.readOnly = readOnly;
            this.useModelRedirectSystem = useModelRedirectSystem;
        }

        public DyeItem createDyeItem(DyeColorant dyeColorant, Item.Settings itemSettings) {
            Identifier identifier = new Identifier(dyeColorant.getId().getNamespace(), dyeColorant.getId().getPath() + "_dye");

            if (readOnly && Objects.equals(dyeColorant.getId().getNamespace(), "minecraft")) {
                return (DyeItem) Registry.ITEM.get(identifier);
            }

            DyeItem dyeItem = Registry.register(Registry.ITEM, identifier, new JelloDyeItem(dyeColorant, itemSettings));

            TagInjector.injectItems(JelloTags.Items.DYE.id(), dyeItem);

            return dyeItem;
        }
    }
}
