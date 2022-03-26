package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DyedVariantContainer {

    public static final Map<DyeColorant, DyedVariantContainer> DYED_VARIANTS = new HashMap<>();

    public Map<DyeableBlockVariant, Block> dyedBlocks;
    public DyeItem dyeItem;

    public BlockBuilder blockBuilder;
    public ItemBuilder itemBuilder;

    protected DyedVariantContainer(Map<DyeableBlockVariant, Block> dyedBlocks, DyeItem dyeItem, BlockBuilder blockBuilder, ItemBuilder itemBuilder){
        this.dyedBlocks = dyedBlocks;
        this.dyeItem = dyeItem;
        this.blockBuilder = blockBuilder;
        this.itemBuilder = itemBuilder;
    }

    //TODO: FILTER THE ENTRY'S TO BE IN A CERTAIN COLOR ORDERED???
    protected static void updateExistingContainers(DyeableBlockVariant dyeableBlockVariant){
        for(Map.Entry<DyeColorant, DyedVariantContainer> entry : DYED_VARIANTS.entrySet()){
            DyeColorant dyeColorant = entry.getKey();

            entry.getValue().addToExistingContainer(dyeColorant, dyeableBlockVariant);
        }
    }

    private DyedVariantContainer addToExistingContainer(DyeColorant dyeColorant, DyeableBlockVariant dyeableBlockVariant){
        this.dyedBlocks.put(dyeableBlockVariant, this.blockBuilder.createNewBlockVariant(dyeColorant, dyeableBlockVariant));

        return this;
    }

    @ApiStatus.Internal
    public static DyedVariantContainer createVariantContainer(DyeColorant dyeColorant){
        return createVariantContainer(dyeColorant, null,  null, true, false);
    }

    public static DyedVariantContainer createVariantContainer(DyeColorant dyeColorant, Item.Settings dyeItemSettings, boolean useModelRedirectSystem){
        return createVariantContainer(dyeColorant, dyeItemSettings, null, false, useModelRedirectSystem);
    }

    public static DyedVariantContainer createVariantContainer(DyeColorant dyeColorant, Item.Settings dyeItemSettings, boolean readOnly, boolean useModelRedirectSystem){
        return createVariantContainer(dyeColorant, dyeItemSettings, null, readOnly, useModelRedirectSystem);
    }

    public static DyedVariantContainer createVariantContainer(DyeColorant dyeColorant, Item.Settings dyeItemSettings, OwoItemSettings owoItemSettings, boolean readOnly, boolean useModelRedirectSystem){
        BlockBuilder blockBuilder = new BlockBuilder(readOnly, useModelRedirectSystem);
        ItemBuilder itemBuilder = new ItemBuilder(readOnly, useModelRedirectSystem);

        Map<DyeableBlockVariant, Block> dyedBlocks = blockBuilder.createVariantMapFromDye(dyeColorant, owoItemSettings);
        DyeItem dyeItem = itemBuilder.createDyeItem(dyeColorant, dyeItemSettings);

        DyedVariantContainer dyedVariantContainer = new DyedVariantContainer(dyedBlocks, dyeItem, blockBuilder, itemBuilder);

        DYED_VARIANTS.put(dyeColorant, dyedVariantContainer);

        ColorBlockRegistry.registerDyeColorant(dyeColorant, dyedVariantContainer);

        return dyedVariantContainer;
    }

    public static class BlockBuilder{
        public boolean readOnly;
        public final boolean useModelRedirectSystem;

        public BlockBuilder(boolean readOnly, boolean useModelRedirectSystem){
            this.readOnly = readOnly;
            this.useModelRedirectSystem = useModelRedirectSystem;
        }

        public Map<DyeableBlockVariant, Block> createVariantMapFromDye(DyeColorant dyeColorant){
            return this.createVariantMapFromDye(dyeColorant, null);
        }

        protected Map<DyeableBlockVariant, Block> createVariantMapFromDye(DyeColorant dyeColorant, @Nullable OwoItemSettings overrideSettings){
            Map<DyeableBlockVariant, Block> dyedBlocks = new HashMap<>();

            for(DyeableBlockVariant dyeableBlockVariant : VanillaBlockVariants.VANILLA_VARIANTS){
                DyeableBlockVariant.RegistryInfo info = null;
                Block block;

                if(!readOnly) {
                    info = dyeableBlockVariant.makeChildBlock(dyeColorant);

                    if(overrideSettings != null){
                        info.setOverrideSettings(overrideSettings);
                    }

                    block = registerBlock(dyeableBlockVariant, info, dyeColorant);

                    dyeableBlockVariant.addToBlockTags(block);
                    dyeableBlockVariant.addToItemTags(block.asItem());
                }else{
                    block = registerBlock(dyeableBlockVariant, info, dyeColorant);
                }



                dyedBlocks.put(dyeableBlockVariant, block);
            }

            this.readOnly = false;



            return dyedBlocks;
        }

        private Block createNewBlockVariant(DyeColorant dyeColorant, DyeableBlockVariant dyeableBlockVariant){
            DyeableBlockVariant.RegistryInfo info = dyeableBlockVariant.makeChildBlock(dyeColorant);

            Block block = registerBlock(dyeableBlockVariant, info, dyeColorant);

            dyeableBlockVariant.addToBlockTags(block);
            dyeableBlockVariant.addToItemTags(block.asItem());

            return block;
        }

        private Block registerBlock(DyeableBlockVariant dyeableBlockVariant, @Nullable DyeableBlockVariant.RegistryInfo registryInfo, DyeColorant dyeColorant) {
            if(readOnly && Objects.equals(dyeColorant.getId().getNamespace(), "minecraft")){
                return dyeableBlockVariant.getBlockVariant(dyeColorant);
            }

            String nameSpace;

            if(Objects.equals(dyeableBlockVariant.variantIdentifier.getNamespace(), "minecraft")){
                nameSpace = dyeColorant.getId().getNamespace();
            }else{
                nameSpace = dyeableBlockVariant.variantIdentifier.getNamespace();
            }

            Identifier identifier = new Identifier(nameSpace, dyeableBlockVariant.getBlockVariantPath(dyeColorant));

            if(this.useModelRedirectSystem){
                DyeColorantRegistry.IDENTIFIER_RESOURCE_REDIRECTS.add(identifier);
            }

            Block block = Registry.register(Registry.BLOCK, identifier, registryInfo.block);

            if (!registryInfo.noBlockItem()) {
                Registry.register(Registry.ITEM, identifier, dyeableBlockVariant.blockItemMaker.createBlockItemFromDyeColor(dyeColorant, block, registryInfo.getItemSettings()));
            }

            return block;
        }
    }

    public static class ItemBuilder{
        public boolean readOnly;
        public final boolean useModelRedirectSystem;

        public ItemBuilder(boolean readOnly, boolean useModelRedirectSystem){
            this.readOnly = readOnly;
            this.useModelRedirectSystem = useModelRedirectSystem;
        }

        public DyeItem createDyeItem(DyeColorant dyeColorant, Item.Settings itemSettings){
            Identifier identifier = new Identifier(dyeColorant.getId().getNamespace(), dyeColorant.getId().getPath() + "_dye");

            if(readOnly && Objects.equals(dyeColorant.getId().getNamespace(), "minecraft")){
                return (DyeItem) Registry.ITEM.get(identifier);
            }

            return Registry.register(Registry.ITEM, identifier, new io.wispforest.jello.api.dye.item.DyeItem(dyeColorant, itemSettings));
        }
    }
}
