package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.item.ColoredBlockItem;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.api.item.JelloItemSettings;
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
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A {@link DyeableBlockVariant} is a way to add your own
 * Dyed Block Variants, like Minecraft's Wool and Concrete,
 * to Jello's System so that any {@link DyeColorant}
 * created gets made with your Variant.
 */
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

    private Identifier defaultBlock;

    public final RecursiveType recursiveType;
    public final @Nullable Supplier<DyeableBlockVariant> childVariant;

    private final BlockMaker blockMaker;
    public BlockItemSettings defaultSettings;

    public final boolean createBlockItem;
    private BlockItemMaker blockItemMaker;

    private TagKey<Block> primaryBlockTag;
    private final Set<TagKey<Block>> secondaryBlockTags = new HashSet<>();

    private TagKey<Item> primaryItemTag;
    private final Set<TagKey<Item>> secondaryItemTags = new HashSet<>();

    private DyeableBlockVariant(Identifier variantIdentifier, @Nullable Supplier<DyeableBlockVariant> possibleChildVariant, boolean noBlockItem, @Nullable ItemGroup defaultGroup, BlockMaker blockMaker) {
        this.variantIdentifier = variantIdentifier;
        this.blockMaker = blockMaker;
        this.createBlockItem = noBlockItem;

        String[] partParts = variantIdentifier.getPath().split("_");
        this.wordCount = partParts.length;

        this.defaultSettings = defaultGroup != null ? BlockItemSettings.of(defaultGroup) : BlockItemSettings.of();

        if (possibleChildVariant != null) {
            this.recursiveType = RecursiveType.CHAINED;
            this.childVariant = possibleChildVariant;
        } else {
            this.recursiveType = RecursiveType.NONE;
            this.childVariant = null;
        }

        this.defaultBlock = new Identifier(variantIdentifier.getNamespace(), "white_" + variantIdentifier.getPath());
        this.blockItemMaker = BlockItemMaker.DEFAULT;
    }

    //---------------------------------------------------------------------------------------------------

    public static DyeableBlockVariant of(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant, boolean noBlockItem, ItemGroup defaultGroup, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, noBlockItem, defaultGroup, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant, ItemGroup defaultGroup, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, true, defaultGroup, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, boolean noBlockItem, ItemGroup defaultGroup, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, null, noBlockItem, defaultGroup, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, ItemGroup defaultGroup, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, null, true, defaultGroup, blockMaker);
    }

    //---------------------------------------------------------------------------------------------------

    /**
     * Sets the stack count of the for the {@link BlockItem} if such will be created
     *
     * @param maxCount
     */
    public DyeableBlockVariant stackCount(int maxCount) {
        this.defaultSettings.setItemStackCount(maxCount);

        return this;
    }

    /**
     * Sets the BlockItem will be a Fire Proof Item
     */
    public DyeableBlockVariant fireproof() {
        this.defaultSettings.fireproof = true;

        return this;
    }

    /**
     * Adds a {@link FoodComponent} to the {@link BlockItem} if such will be created
     *
     * @param foodComponent The FoodComponent being added to the BlockItem
     */
    public DyeableBlockVariant setFoodComponent(FoodComponent foodComponent) {
        this.defaultSettings.foodComponent = foodComponent;

        return this;
    }

    /**
     * Manually change the {@link #defaultBlock} Identifier
     *
     * @param identifier The identifier of the block
     */
    public final DyeableBlockVariant setDefaultBlock(Identifier identifier) {
        this.defaultBlock = identifier;

        return this;
    }

    /**
     * Manually change the {@link #defaultBlock} Identifier by combining the Block's path and the variant's MODID
     *
     * @param path The Block's default path
     */
    public final DyeableBlockVariant setDefaultBlock(String path) {
        this.defaultBlock = new Identifier(variantIdentifier.getNamespace(), path);

        return this;
    }

    /**
     * Manually change the {@link #blockItemMaker} if a custom one is needed
     *
     * @param blockItemMaker Custom BlockItemMaker
     */
    public final DyeableBlockVariant setBlockItemMaker(BlockItemMaker blockItemMaker) {
        this.blockItemMaker = blockItemMaker;

        return this;
    }

    /**
     * Add all tags needed for this Block to be added too.
     * You will need at least one Tag which this block variant is linked too or the {@link #addToBlockTags} will throw a {@link NullPointerException}
     *
     * @param tags Tags to be added to when the block is built
     */
    @SafeVarargs
    public final DyeableBlockVariant setBlockTags(TagKey<Block>... tags) {
        for (int i = 0; i < tags.length; i++) {
            if (i == 0) {
                primaryBlockTag = tags[i];
            } else {
                secondaryBlockTags.add(tags[i]);
            }
        }

        return this;
    }

    /**
     * Add all tags needed for the Created {@link BlockItem} if such is made
     *
     * @param tags Tags to be added to when the {@link BlockItem} is built
     */
    @SafeVarargs
    public final DyeableBlockVariant setItemTags(TagKey<Item>... tags) {
        for (int i = 0; i < tags.length; i++) {
            if (i == 0) {
                primaryItemTag = tags[i];
            } else {
                secondaryItemTags.add(tags[i]);
            }
        }

        return this;
    }

    /**
     * Method must be called when the Variant is finished being edited
     * Will add your variant to the {@link #ADDITION_BLOCK_VARIANTS} and
     * retroactively add this {@link DyeableBlockVariant} and {@link DyedVariantContainer#updateExistingContainers}
     */
    public final DyeableBlockVariant register() {
        if (!DyeableBlockVariant.ADDITION_BLOCK_VARIANTS.contains(this)) {
            DyedVariantContainer.updateExistingContainers(this);
        }

        ColorBlockRegistry.registerBlockTypeWithRecursion(this);
        DyeableBlockVariant.ADDITION_BLOCK_VARIANTS.add(this);

        return this;
    }

    //---------------------------------------------------------------------------------------------------

    public final TagKey<Block> getPrimaryBlockTag() {
        return this.primaryBlockTag;
    }

    public Block getBlockVariant(DyeColorant dyeColorant) {
        return Registry.BLOCK.get(new Identifier(this.variantIdentifier.getNamespace(), getBlockVariantPath(dyeColorant)));
    }

    public Block getDefaultBlockVariant() {
        return Registry.BLOCK.get(this.defaultBlock);
    }

    public String getBlockVariantPath(DyeColorant dyeColorant) {
        return dyeColorant.getName() + "_" + this.variantIdentifier.getPath();
    }

    public boolean isIdentifierAVariant(Identifier identifier, boolean isItem) {
        if (isItem && !this.createBlockItem) {
            return false;
        }

        String[] pathParts = identifier.getPath().split("_");

        if (pathParts.length <= wordCount) {
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = pathParts.length - wordCount; i < pathParts.length; i++) {
            stringBuilder.append(pathParts[i]);

            if (i < pathParts.length - 1) {
                stringBuilder.append("_");
            }
        }

        return stringBuilder.toString().equals(this.variantIdentifier.getPath());
    }

    //---------------------------------------------------------------------------------------------------

    @ApiStatus.Internal
    protected static DyeableBlockVariant of(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, true, null, blockMaker);
    }

    @ApiStatus.Internal
    protected static DyeableBlockVariant of(Identifier variantIdentifier, boolean noBlockItem, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, null, noBlockItem, null, blockMaker);
    }

    @ApiStatus.Internal
    protected static DyeableBlockVariant of(Identifier variantIdentifier, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, null, true, null, blockMaker);
    }

    @ApiStatus.Internal
    protected final void addToItemTags(Item item) {
        if (primaryItemTag != null) {
            TagInjector.injectItems(primaryItemTag.id(), item);
        }

        for (TagKey<Item> tagKey : secondaryItemTags) {
            TagInjector.injectItems(tagKey.id(), item);
        }
    }

    @ApiStatus.Internal
    protected final void addToBlockTags(Block block) {
        if (primaryBlockTag != null) {
            TagInjector.injectBlocks(primaryBlockTag.id(), block);
        } else {
            throw new NullPointerException("You need to at least set one block tag that this variant will use to add all the blocks generated! Variant:" + this.variantIdentifier);
        }

        for (TagKey<Block> tagKey : secondaryBlockTags) {
            TagInjector.injectBlocks(tagKey.id(), block);
        }
    }

    @ApiStatus.Internal
    protected RegistryInfo makeBlock(DyeColorant dyeColorant) {
        return this.makeChildBlock(dyeColorant, null);
    }

    @ApiStatus.Internal
    protected RegistryInfo makeChildBlock(DyeColorant dyeColorant, @Nullable Block parentBlock) {
        Block returnBlock = blockMaker.createBlockFromDyeColor(dyeColorant, parentBlock);

        if (!createBlockItem) {
            return RegistryInfo.of(returnBlock, null);
        } else {
            return RegistryInfo.of(returnBlock, defaultSettings);
        }
    }

    @ApiStatus.Internal
    protected BlockItem makeBlockItem(DyeColorant dyeColorant, Block block, Item.Settings settings) {
        return this.blockItemMaker.createBlockItemFromDyeColor(dyeColorant, block, settings);
    }

    public interface BlockMaker {
        Block createBlockFromDyeColor(DyeColorant dyeColorant, @Nullable Block parentBlock);
    }

    public interface BlockItemMaker {
        BlockItemMaker DEFAULT = (dyeColorant, block, settings) -> new ColoredBlockItem(block, settings);

        BlockItem createBlockItemFromDyeColor(DyeColorant dyeColorant, Block block, Item.Settings settings);
    }

    //---------------------------------------------------------------------------------------------------

    private static class BlockItemSettings {
        public int maxCount;
        public boolean fireproof;
        @Nullable public FoodComponent foodComponent;
        public ItemGroup group;

        private BlockItemSettings(int maxCount, boolean fireproof, FoodComponent foodComponent, ItemGroup group) {
            this.maxCount = maxCount;
            this.fireproof = fireproof;
            this.foodComponent = foodComponent;
            this.group = group;
        }

        private static BlockItemSettings of() {
            return new BlockItemSettings(64, false, null, null);
        }

        private static BlockItemSettings of(ItemGroup group) {
            return new BlockItemSettings(64, false, null, group);
        }

        private void setItemStackCount(int count) {
            this.maxCount = count;
        }
    }

    //---------------------------------------------------------------------------------------------------

    protected static class RegistryInfo {
        public final Block block;
        public final boolean noBlockItem;
        private final BlockItemSettings settings;

        protected OwoItemSettings overrideSettings = null;

        private RegistryInfo(Block block, boolean noBlockItem, BlockItemSettings blockItemSettings) {
            this.block = block;
            this.noBlockItem = noBlockItem;
            this.settings = blockItemSettings;
        }

        protected boolean noBlockItem() {
            return this.noBlockItem;
        }

        private static RegistryInfo of(Block block, @Nullable BlockItemSettings blockItemSettings) {
            if (blockItemSettings == null) {
                return new RegistryInfo(block, true, null);
            }

            return new RegistryInfo(block, false, blockItemSettings);
        }

        protected void setOverrideSettings(OwoItemSettings owoItemSettings) {
            this.overrideSettings = owoItemSettings;
        }

        protected Item.Settings getItemSettings() {
            Item.Settings settings = overrideSettings == null ? new Item.Settings() : JelloItemSettings.copyFrom(this.overrideSettings);

            settings.maxCount(this.settings.maxCount);

            if (this.settings.group != null) {
                settings.group(this.settings.group);
            }

            if (this.settings.foodComponent != null) {
                settings.food(this.settings.foodComponent);
            }

            if (this.settings.fireproof) {
                settings.fireproof();
            }

            return settings;
        }
    }
}
