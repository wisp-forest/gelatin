package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.item.DyeableItemVariant;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Main Class for storing all data on all registered {@link DyeColorant} used to create colored {@link DyeableBlockVariant}
 * <p>Look at {@link DyeColorantRegistry#createDyedVariants(DyeColorant, Item.Settings, boolean)} if you want to create variants off of your {@link DyeColorant}</p>
 */
public class DyeableVariantManager {

    public record DyeColorantVariantData(Map<DyeableBlockVariant, Block> dyedBlocks, Map<DyeableItemVariant, Item> dyedItems,  BlockBuilder blockBuilder, ItemBuilder itemBuilder){
        private void addToExistingContainerWithRecursionBlock(DyeColorant dyeColorant, DyeableBlockVariant dyeableBlockVariant) {
            this.blockBuilder.recursivelyBuildBlocksFromVariant(this.dyedBlocks, null, dyeableBlockVariant, dyeColorant, null);
        }

        private void addToExistingContainerWithRecursionItem(DyeColorant dyeColorant, DyeableItemVariant dyeableItemVariant) {
            this.itemBuilder.recursivelyBuildItemsFromVariant(this.dyedItems, null, dyeableItemVariant, dyeColorant, null);
        }

        public Item dyeItem(){
            return this.dyedItems().get(VanillaItemVariants.DYE);
        }
    }

    protected static final Map<DyeColorant, DyeColorantVariantData> DYED_VARIANTS = new HashMap<>();

    protected DyeableVariantManager() {}

    /**
     * Get the Block Based on the given {@link DyeColorant} and {@link DyeableBlockVariant}
     *
     * @param dyeColorant         The Color of the block to be searched for
     * @param dyeableBlockVariant The Block Variant the block is of
     * @return either the block found or {@link Blocks#AIR} if no block was found
     */
    public static Block getDyedBlockVariant(@Nonnull DyeColorant dyeColorant, @Nonnull DyeableBlockVariant dyeableBlockVariant) {
        for (Map.Entry<DyeColorant, DyeColorantVariantData> variantContainerEntry : DYED_VARIANTS.entrySet()) {
            DyeColorant possibleDyeColorant = variantContainerEntry.getKey();

            if (dyeColorant == possibleDyeColorant) {
                DyeColorantVariantData container = variantContainerEntry.getValue();

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
     * @return {@link DyeableVariantManager} if found or null by default
     */
    @Nullable
    public static DyeColorantVariantData getContainer(DyeColorant dyeColorant) {
        return DYED_VARIANTS.get(dyeColorant);
    }

    /**
     * Way of accessing the Variant Map without Direct access
     *
     * @return the current Map of all {@link DyeableVariantManager} made
     */
    public static Map<DyeColorant, DyeColorantVariantData> getVariantMap() {
        return DYED_VARIANTS;
    }

    //-----------------------------------------------------------------------------------------------------

    @ApiStatus.Internal
    public static DyeColorantVariantData createVariantContainer(DyeColorant dyeColorant) {
        return createVariantContainer(dyeColorant, null, null, true, false);
    }

    @ApiStatus.Internal
    public static DyeColorantVariantData createVariantContainer(DyeColorant dyeColorant, Item.Settings dyeItemSettings, boolean useModelRedirectSystem) {
        return createVariantContainer(dyeColorant, dyeItemSettings, null, false, useModelRedirectSystem);
    }

    @ApiStatus.Internal
    public static DyeColorantVariantData createVariantContainer(DyeColorant dyeColorant, Item.Settings dyeItemSettings, OwoItemSettings owoItemSettings, boolean readOnly, boolean useModelRedirectSystem) {
        BlockBuilder blockBuilder = new BlockBuilder(readOnly, useModelRedirectSystem);
        ItemBuilder itemBuilder = new ItemBuilder(readOnly, useModelRedirectSystem);

        Map<DyeableBlockVariant, Block> dyedBlocks = blockBuilder.buildVariantMapFromDye(dyeColorant, owoItemSettings);
        Map<DyeableItemVariant, Item> dyedItems = itemBuilder.buildVariantMapFromDye(dyeColorant, owoItemSettings);
//        DyeItem dyeItem = itemBuilder.createDyeItem(dyeColorant, dyeItemSettings);

        DyeColorantVariantData dyedVariantContainer = new DyeColorantVariantData(dyedBlocks, dyedItems, blockBuilder, itemBuilder);

        DYED_VARIANTS.put(dyeColorant, dyedVariantContainer);

        //ColorBlockRegistry.registerDyeColorant(dyeColorant, dyedVariantContainer);

        return dyedVariantContainer;
    }

    //-----------------------------------------------------------------------------------------------------

    //TODO: FILTER THE ENTRIES TO BE IN A CERTAIN COLOR ORDERED???
    @ApiStatus.Internal
    public static void updateExistingDataForBlock(DyeableBlockVariant dyeableBlockVariant) {
        for (Map.Entry<DyeColorant, DyeColorantVariantData> entry : DYED_VARIANTS.entrySet()) {
            DyeColorant dyeColorant = entry.getKey();

            //Skips if the DyeableBlockVariant doesn't want any modded dyes to be added
            if(!DyeColorantRegistry.Constants.VANILLA_DYES.contains(dyeColorant) && dyeableBlockVariant.vanillaDyesOnly())
                continue;

            entry.getValue().addToExistingContainerWithRecursionBlock(dyeColorant, dyeableBlockVariant);
        }
    }

    //TODO: FILTER THE ENTRIES TO BE IN A CERTAIN COLOR ORDERED???
    @ApiStatus.Internal
    public static void updateExistingDataForItem(DyeableItemVariant dyeableItemVariant) {
        for (Map.Entry<DyeColorant, DyeColorantVariantData> entry : DYED_VARIANTS.entrySet()) {
            DyeColorant dyeColorant = entry.getKey();

            //Skips if the DyeableBlockVariant doesn't want any modded dyes to be added
            if(!DyeColorantRegistry.Constants.VANILLA_DYES.contains(dyeColorant) && dyeableItemVariant.vanillaDyesOnly())
                continue;

            entry.getValue().addToExistingContainerWithRecursionItem(dyeColorant, dyeableItemVariant);
        }
    }

    /**
     * Block Builder class used for the Initial creation of the {@link DyeableBlockVariant} and furthermore when a new {@link DyeColorant} is registered if the variant allows for it
     */
    public static class BlockBuilder {
        public boolean readOnly;
        public final boolean useModelRedirectSystem;

        public BlockBuilder(boolean readOnly, boolean useModelRedirectSystem) {
            this.readOnly = readOnly;
            this.useModelRedirectSystem = useModelRedirectSystem;
        }

        protected Map<DyeableBlockVariant, Block> buildVariantMapFromDye(DyeColorant dyeColorant, @Nullable OwoItemSettings overrideSettings) {
            Map<DyeableBlockVariant, Block> dyedBlocks = new HashMap<>();

            for (DyeableBlockVariant dyeableBlockVariant : DyeableBlockVariant.AllBaseBlockVariants()) {
                this.recursivelyBuildBlocksFromVariant(dyedBlocks, null, dyeableBlockVariant, dyeColorant, overrideSettings);
            }

            this.readOnly = false;

            return dyedBlocks;
        }

        private void recursivelyBuildBlocksFromVariant(Map<DyeableBlockVariant, Block> dyedBlocks, Block possibleParentBlock, DyeableBlockVariant parentBlockVariant, DyeColorant dyeColorant, @Nullable OwoItemSettings overrideSettings) {
            Pair<Block, Item.Settings> info = null;

            if (!(isReadOnly(parentBlockVariant) || (Objects.equals(dyeColorant.getId().getNamespace(), "minecraft") && Objects.equals(parentBlockVariant.variantIdentifier.getNamespace(), "minecraft")))) {
                info = parentBlockVariant.makeChildBlock(dyeColorant, possibleParentBlock);

                if (overrideSettings != null)
                    info.setRight(overrideSettings);
            }

            Block childBlock = registerBlock(parentBlockVariant, info, dyeColorant);

            parentBlockVariant.addToTags(childBlock, isReadOnly(parentBlockVariant));

            dyedBlocks.put(parentBlockVariant, childBlock);

            if (parentBlockVariant.childVariant.get() != null) {
                DyeableBlockVariant childBlockVariant = parentBlockVariant.childVariant.get();

                recursivelyBuildBlocksFromVariant(dyedBlocks, childBlock, childBlockVariant, dyeColorant, overrideSettings);
            }
        }

        private Block registerBlock(DyeableBlockVariant dyeableBlockVariant, @Nullable Pair<Block, Item.Settings> registryInfo, DyeColorant dyeColorant) {
            if (registryInfo == null)
                return dyeableBlockVariant.getColoredEntry(dyeColorant);

            String nameSpace = Objects.equals(dyeableBlockVariant.variantIdentifier.getNamespace(), "minecraft") ?
                    dyeColorant.getId().getNamespace() :
                    dyeableBlockVariant.variantIdentifier.getNamespace();

            Identifier identifier = new Identifier(nameSpace, dyeableBlockVariant.getColoredBlockPath(dyeColorant));

            addToModelRedirectSystem(identifier);

            Block block = Registry.register(Registry.BLOCK, identifier, registryInfo.getLeft());

            if (dyeableBlockVariant.createBlockItem()) {
                Registry.register(Registry.ITEM, identifier, dyeableBlockVariant.makeBlockItem(dyeColorant, block, registryInfo.getRight()));
            }

            return block;
        }

        private void addToModelRedirectSystem(Identifier identifier){
            if (this.useModelRedirectSystem)
                DyeColorantRegistry.IDENTIFIER_RESOURCE_REDIRECTS.add(identifier);
        }

        private boolean isReadOnly(DyeableBlockVariant variant){
            return readOnly || variant.alwaysReadOnly();
        }
    }

    public static class ItemBuilder {
        public boolean readOnly;
        public final boolean useModelRedirectSystem;

        public ItemBuilder(boolean readOnly, boolean useModelRedirectSystem) {
            this.readOnly = readOnly;
            this.useModelRedirectSystem = useModelRedirectSystem;
        }

        protected Map<DyeableItemVariant, Item> buildVariantMapFromDye(DyeColorant dyeColorant, @Nullable OwoItemSettings overrideSettings) {
            Map<DyeableItemVariant, Item> dyedItems = new HashMap<>();

            for (DyeableItemVariant dyeableItemVariant : VanillaItemVariants.VANILLA_VARIANTS) {
                this.recursivelyBuildItemsFromVariant(dyedItems, null, dyeableItemVariant, dyeColorant, overrideSettings);
            }

            this.readOnly = false;

            return dyedItems;
        }

        private void recursivelyBuildItemsFromVariant(Map<DyeableItemVariant, Item> dyedBlocks, Item possibleParentItem, DyeableItemVariant parentItemVariant, DyeColorant dyeColorant, @Nullable OwoItemSettings overrideSettings) {
            Item item = null;

            if (!(isReadOnly(parentItemVariant) || (Objects.equals(dyeColorant.getId().getNamespace(), "minecraft") && Objects.equals(parentItemVariant.variantIdentifier.getNamespace(), "minecraft")))) {
                item = parentItemVariant.makeItem(dyeColorant, possibleParentItem, overrideSettings != null ? overrideSettings : parentItemVariant.defaultItemSettings);
            }

            item = registerItem(parentItemVariant, item, dyeColorant);

            parentItemVariant.addToTags(item, isReadOnly(parentItemVariant));

            dyedBlocks.put(parentItemVariant, item);

            if (parentItemVariant.childVariant.get() != null) {
                DyeableItemVariant childItemVariant = parentItemVariant.childVariant.get();

                recursivelyBuildItemsFromVariant(dyedBlocks, item, childItemVariant, dyeColorant, overrideSettings);
            }
        }

        private Item registerItem(DyeableItemVariant dyeableItemVariant, @Nullable Item item, DyeColorant dyeColorant) {
            if (item == null)
                return dyeableItemVariant.getColoredEntry(dyeColorant);

            String nameSpace = Objects.equals(dyeableItemVariant.variantIdentifier.getNamespace(), "minecraft") ?
                    dyeColorant.getId().getNamespace() :
                    dyeableItemVariant.variantIdentifier.getNamespace();

            Identifier identifier = new Identifier(nameSpace, dyeableItemVariant.getColoredEntryPath(dyeColorant));

            addToModelRedirectSystem(identifier);

            return Registry.register(Registry.ITEM, identifier, item);
        }

        private void addToModelRedirectSystem(Identifier identifier){
            if (this.useModelRedirectSystem)
                DyeColorantRegistry.IDENTIFIER_RESOURCE_REDIRECTS.add(identifier);
        }

        private boolean isReadOnly(DyeableItemVariant variant){
            return readOnly || variant.alwaysReadOnly();
        }
    }

//    /**
//     * Will be similar to Block Builder within the future
//     */
//    public static class ItemBuilder {
//        public boolean readOnly;
//        public final boolean useModelRedirectSystem;
//
//        public ItemBuilder(boolean readOnly, boolean useModelRedirectSystem) {
//            this.readOnly = readOnly;
//            this.useModelRedirectSystem = useModelRedirectSystem;
//        }
//
//        public DyeItem createDyeItem(DyeColorant dyeColorant, Item.Settings itemSettings) {
//            Identifier identifier = new Identifier(dyeColorant.getId().getNamespace(), dyeColorant.getId().getPath() + "_dye");
//
//            if (readOnly && Objects.equals(dyeColorant.getId().getNamespace(), "minecraft")) {
//                return (DyeItem) Registry.ITEM.get(identifier);
//            }
//
//            DyeItem dyeItem = Registry.register(Registry.ITEM, identifier, new JelloDyeItem(dyeColorant, itemSettings));
//
//            TagInjector.inject(Registry.ITEM, JelloTags.Items.DYE.id(), dyeItem);
//
//            return dyeItem;
//        }
//    }
}
