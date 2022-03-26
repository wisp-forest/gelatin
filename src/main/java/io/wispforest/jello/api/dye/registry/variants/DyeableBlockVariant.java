package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.item.ColoredBlockItem;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.api.util.JelloItemSettingsOps;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class DyeableBlockVariant {

    public static final Set<DyeableBlockVariant> ADDITION_BLOCK_VARIANTS = new HashSet<>();

    /**
     * None: The type telling the builder that is a single block and has no other variants that depend on this block to build
     * Chain: The type telling the builder that this {@link BlockMaker} needs a block to be created first
     */
    public enum RecursiveType {
        NONE(),
        CHAINED()
    }

    public final Identifier variantIdentifier;
    public final int wordCount;

    public Identifier defaultBlock;

    public final RecursiveType recursiveType;
    public final @Nullable Supplier<DyeableBlockVariant> childVariant;

    public final BlockMaker blockMaker;
    public BlockItemSettings defaultSettings;
    public final boolean noBlockItem;

    public BlockItemMaker blockItemMaker;

    private TagKey<Block> primaryBlockTag;
    private Set<TagKey<Block>> secondaryBlockTags = new HashSet<>();

    private TagKey<Item> primaryItemTag;
    private Set<TagKey<Item>> secondaryItemTags = new HashSet<>();

    public DyeableBlockVariant(Identifier variantIdentifier, @Nullable Supplier<DyeableBlockVariant> possibleChildVariant, boolean noBlockItem, @Nullable ItemGroup defaultGroup, BlockMaker blockMaker){
        this.variantIdentifier = variantIdentifier;
        this.blockMaker = blockMaker;
        this.noBlockItem = noBlockItem;

        String[] partParts = variantIdentifier.getPath().split("_");
        this.wordCount = partParts.length;

        if(defaultGroup != null) {
            this.defaultSettings = BlockItemSettings.of(defaultGroup);
        }else{
            this.defaultSettings = BlockItemSettings.of();
        }

        if(possibleChildVariant != null){
            this.recursiveType = RecursiveType.CHAINED;
            this.childVariant = possibleChildVariant;
        }else{
            this.recursiveType = RecursiveType.NONE;
            this.childVariant = null;
        }

        this.defaultBlock = new Identifier(variantIdentifier.getNamespace(), "white_" + variantIdentifier.getPath());
        this.blockItemMaker = BlockItemMaker.DEFAULT;
    }

    //---------------------------------------------------------------------------------------------------

    public static DyeableBlockVariant of(Identifier variantIdentifier, boolean noBlockItem, ItemGroup defaultGroup, BlockMaker blockMaker){
        return new DyeableBlockVariant(variantIdentifier, null, noBlockItem, defaultGroup, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, ItemGroup defaultGroup, BlockMaker blockMaker){
        return new DyeableBlockVariant(variantIdentifier, null, true, defaultGroup, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, boolean noBlockItem,  BlockMaker blockMaker){
        return new DyeableBlockVariant(variantIdentifier, null, noBlockItem, null, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, BlockMaker blockMaker){
        return new DyeableBlockVariant(variantIdentifier, null, true, null, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant, boolean noBlockItem, ItemGroup defaultGroup, BlockMaker blockMaker){
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, noBlockItem, defaultGroup, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant, ItemGroup defaultGroup, BlockMaker blockMaker){
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, true, defaultGroup, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant, boolean noBlockItem,  BlockMaker blockMaker){
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, noBlockItem, null, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant, BlockMaker blockMaker){
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, true, null, blockMaker);
    }

    //---------------------------------------------------------------------------------------------------

    public DyeableBlockVariant stackCount(int maxCount){
        this.defaultSettings.setItemStackCount(maxCount);

        return this;
    }

    public DyeableBlockVariant isFireProof(){
        this.defaultSettings.fireproof = true;

        return this;
    }

    public DyeableBlockVariant setFoodComponent(FoodComponent foodComponent){
        this.defaultSettings.foodComponent = foodComponent;

        return this;
    }

    public final DyeableBlockVariant setDefaultBlock(String path){
        this.defaultBlock = new Identifier(variantIdentifier.getNamespace(), path);

        return this;
    }

    public final DyeableBlockVariant setBlockItemMaker(BlockItemMaker blockItemMaker){
        this.blockItemMaker = blockItemMaker;

        return this;
    }

    //---------------------------------------------------------------------------------------------------

    public final TagKey<Block> getPrimaryBlockTag(){
        return this.primaryBlockTag;
    }

    @SafeVarargs
    public final DyeableBlockVariant setBlockTags(TagKey<Block>... tags){
        for(int i = 0; i < tags.length; i++){
            if(i == 0){
                primaryBlockTag = tags[i];
            }else{
                secondaryBlockTags.add(tags[i]);
            }
        }

        return this;
    }

    @SafeVarargs
    public final DyeableBlockVariant setItemTags(TagKey<Item>... tags){
        for(int i = 0; i < tags.length; i++){
            if(i == 0){
                primaryItemTag = tags[i];
            }else{
                secondaryItemTags.add(tags[i]);
            }
        }

        return this;
    }

    public final void addToItemTags(Item item){
        if(primaryItemTag != null) {
            TagInjector.injectItems(primaryItemTag.id(), item);
        }

        for(TagKey<Item> tagKey : secondaryItemTags){
            TagInjector.injectItems(tagKey.id(), item);
        }
    }

    public final void addToBlockTags(Block block){
        if(primaryBlockTag != null) {
            TagInjector.injectBlocks(primaryBlockTag.id(), block);
        }else{
            throw new NullPointerException("You need to at least set one block tag that this variant will use to add all the blocks generated! Variant:" + this.variantIdentifier);
        }

        for(TagKey<Block> tagKey : secondaryBlockTags){
            TagInjector.injectBlocks(tagKey.id(), block);
        }
    }

    //---------------------------------------------------------------------------------------------------

    public final DyeableBlockVariant registerVariant(){
        if(!DyeableBlockVariant.ADDITION_BLOCK_VARIANTS.contains(this)){
            DyedVariantContainer.updateExistingContainers(this);
        }

        ColorBlockRegistry.registerBlockType(this);
        DyeableBlockVariant.ADDITION_BLOCK_VARIANTS.add(this);

        return this;
    }

    public Block getBlockVariant(DyeColorant dyeColorant){
        return Registry.BLOCK.get(new Identifier(this.variantIdentifier.getNamespace(), getBlockVariantPath(dyeColorant)));
    }

    public String getBlockVariantPath(DyeColorant dyeColorant){
        return dyeColorant.getName() + "_" + this.variantIdentifier.getPath();
    }

    public boolean isIdentifierAVariant(Identifier identifier, boolean isItem){
        if(isItem && this.noBlockItem){
            return false;
        }

        String[] pathParts = identifier.getPath().split("_");

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = pathParts.length - wordCount; i < pathParts.length; i++){
            stringBuilder.append(pathParts[i]);

            if(i < pathParts.length - 1){
                stringBuilder.append("_");
            }
        }

        return stringBuilder.toString().equals(this.variantIdentifier.getPath());
    }

    //---------------------------------------------------------------------------------------------------

    public RegistryInfo makeChildBlock(DyeColorant dyeColorant){
        return this.makeChildBlock(dyeColorant, null);
    }

    public RegistryInfo makeChildBlock(DyeColorant dyeColorant, @Nullable Block parentBlock){
        Block returnBlock = blockMaker.createBlockFromDyeColor(dyeColorant, parentBlock);

        return RegistryInfo.of(returnBlock, defaultSettings);
    }

    public interface BlockMaker {
        Block createBlockFromDyeColor(DyeColorant dyeColorant, @Nullable Block parentBlock);
    }

    public interface BlockItemMaker {
        BlockItemMaker DEFAULT = (dyeColorant, block, settings) -> {
            return new ColoredBlockItem(block, settings);
        };

        BlockItem createBlockItemFromDyeColor(DyeColorant dyeColorant, Block block, Item.Settings settings);
    }

    //---------------------------------------------------------------------------------------------------

    public static class BlockItemSettings{
        public int maxCount;
        public boolean fireproof;
        @Nullable public FoodComponent foodComponent;
        public ItemGroup group;

        public BlockItemSettings(int maxCount, boolean fireproof, FoodComponent foodComponent, ItemGroup group){
            this.maxCount = maxCount;
            this.fireproof = fireproof;
            this.foodComponent = foodComponent;
            this.group = group;
        }

        public static BlockItemSettings of(){
            return new BlockItemSettings(64, false, null, null);
        }

        public static BlockItemSettings of(ItemGroup group){
            return new BlockItemSettings(64, false, null, group);
        }

        public void setItemStackCount(int count){
            this.maxCount = count;
        }
    }

    //---------------------------------------------------------------------------------------------------

    public static class RegistryInfo{

        public final Block block;
        public final boolean noBlockItem;
        public final BlockItemSettings settings;

        protected OwoItemSettings overrideSettings = null;

        public RegistryInfo(Block block, boolean noBlockItem, BlockItemSettings blockItemSettings){
            this.block = block;
            this.noBlockItem = noBlockItem;
            this.settings = blockItemSettings;
        }

        public boolean noBlockItem(){
            return this.noBlockItem;
        }

        public static RegistryInfo of(Block block, @Nullable BlockItemSettings blockItemSettings){
            if(blockItemSettings == null){
                return new RegistryInfo(block, true, null);
            }

            return new RegistryInfo(block, false, blockItemSettings);
        }

        protected void setOverrideSettings(OwoItemSettings owoItemSettings){
            this.overrideSettings = owoItemSettings;
        }

        public Item.Settings getItemSettings(){
            Item.Settings settings = overrideSettings == null ? new Item.Settings() : JelloItemSettingsOps.copyFrom(this.overrideSettings);

            settings.maxCount(this.settings.maxCount);

            if(this.settings.group != null){
                settings.group(this.settings.group);
            }

            if(this.settings.foodComponent != null){
                settings.food(this.settings.foodComponent);
            }

            if(this.settings.fireproof){
                settings.fireproof();
            }

            return settings;
        }
    }
}
