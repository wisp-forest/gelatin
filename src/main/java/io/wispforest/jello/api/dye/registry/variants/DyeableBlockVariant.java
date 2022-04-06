package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.data.tags.JelloTags;
import io.wispforest.jello.api.item.JelloItemSettings;
import io.wispforest.jello.misc.dye.JelloBlockVariants;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link DyeableBlockVariant} is a way to add your own
 * Dyed Block Variants, like Minecraft's Wool and Concrete,
 * to Jello's System so that any {@link DyeColorant}
 * created gets made with your Variant.
 */
public class DyeableBlockVariant {

    public static final Set<DyeableBlockVariant> ALL_BLOCK_VARIANTS = new HashSet<>();
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

    private Identifier defaultBlockIdentifier;
    private boolean addCustomDefaultBlockToTag;

    public final RecursiveType recursiveType;
    public final @Nullable Supplier<DyeableBlockVariant> childVariant;

    private final BlockMaker blockMaker;
    public BlockItemSettings defaultSettings;

    public boolean createBlockItem = true;
    private BlockItemMaker blockItemMaker;

    private boolean vanillaDyeableOnly = false;
    private boolean alwaysReadOnly = false;

    public final TagKey<Block> primaryBlockTag;
    public final Set<TagKey<Block>> secondaryBlockTags = new HashSet<>();

    public final TagKey<Item> primaryItemTag;
    public final Set<TagKey<Item>> secondaryItemTags = new HashSet<>();

    private Function<Block, LootTable> lootTableBuilder = (block) -> BlockLootTableGenerator.drops(block).build();


    /**
     * @param variantIdentifier The {@link Identifier} based off your Modid and the block path for your variant
     * @param possibleChildVariant Any Variant that needs this Block to create itself from
     * @param defaultGroup The {@link ItemGroup} of which this block will be put into
     * @param blockMaker A generalized way of creating your Block Variant (Look at {@link VanillaBlockVariants} or {@link JelloBlockVariants} for a example)
     */
    public DyeableBlockVariant(Identifier variantIdentifier, @Nullable Supplier<DyeableBlockVariant> possibleChildVariant, @Nullable ItemGroup defaultGroup, @Nullable BlockMaker blockMaker) {
        this.variantIdentifier = variantIdentifier;
        this.blockMaker = blockMaker;

        if(blockMaker == null){
            alwaysReadOnly = true;
        }

        this.wordCount = variantIdentifier.getPath().split("_").length;

        this.defaultSettings = defaultGroup != null ? BlockItemSettings.of(defaultGroup) : BlockItemSettings.of();

        if (possibleChildVariant != null) {
            this.recursiveType = RecursiveType.CHAINED;
            this.childVariant = possibleChildVariant;
        } else {
            this.recursiveType = RecursiveType.NONE;
            this.childVariant = null;
        }

        this.defaultBlockIdentifier = new Identifier(variantIdentifier.getNamespace(), "white_" + variantIdentifier.getPath());
        this.blockItemMaker = BlockItemMaker.DEFAULT;

        this.primaryBlockTag = TagKey.of(Registry.BLOCK_KEY, Jello.id(variantIdentifier.getPath()));
        this.primaryItemTag = TagKey.of(Registry.ITEM_KEY, Jello.id(variantIdentifier.getPath()));
    }

    //---------------------------------------------------------------------------------------------------

    public static DyeableBlockVariant of(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant, ItemGroup defaultGroup, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, defaultGroup, blockMaker);
    }

    public static DyeableBlockVariant of(Identifier variantIdentifier, ItemGroup defaultGroup, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, null, defaultGroup, blockMaker);
    }

    /**
     * A way of using the Coloring Events within Jello with only Vanilla Colors and Blocks added by your Mod
     *
     * @param variantIdentifier The {@link Identifier} based off your Modid and the block path for your variant
     */
    public static DyeableBlockVariant readOnly(Identifier variantIdentifier) {
        return new DyeableBlockVariant(variantIdentifier, null, null, null).setVanillaDyeableOnly();
    }

    public static DyeableBlockVariant readOnly(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant) {
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, null, null).setVanillaDyeableOnly();
    }

    //---------------------------------------------------------------------------------------------------

    /**
     * Sets the stack count of the for the {@link BlockItem} if such will be created
     *
     * @param maxCount Maximum Stack Count
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
     * Manually change the {@link #defaultBlockIdentifier} Identifier
     *
     * @param identifier The identifier of the block
     */
    public final DyeableBlockVariant setDefaultBlock(Identifier identifier) {
        this.defaultBlockIdentifier = identifier;
        this.addCustomDefaultBlockToTag = true;

        return this;
    }

    /**
     * Manually change the {@link #defaultBlockIdentifier} Identifier by combining the Block's path and the variant's MODID
     *
     * @param path The Block's default path
     */
    public final DyeableBlockVariant setDefaultBlock(String path) {
        return this.setDefaultBlock(new Identifier(variantIdentifier.getNamespace(), path));
    }

    /**
     * Disables creation of BlockItem for the given variant
     */
    public final DyeableBlockVariant noBlockItem(){
        createBlockItem = false;
        defaultSettings = null;

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
        secondaryBlockTags.addAll(Arrays.asList(tags));

        return this;
    }

    /**
     * Add all tags needed for the Created {@link BlockItem} if such is made
     *
     * @param tags Tags to be added to when the {@link BlockItem} is built
     */
    @SafeVarargs
    public final DyeableBlockVariant setItemTags(TagKey<Item>... tags) {
        secondaryItemTags.addAll(Arrays.asList(tags));

        return this;
    }

    /**
     * Disables the creation of Modded Dyed Variants and only allows for Coloring this block with Vanilla Colors
     */
    public final DyeableBlockVariant setVanillaDyeableOnly(){
        this.vanillaDyeableOnly = true;

        return this;
    }

    /**
     * Change the default Function for automatically generating the {@link LootTable} for this Block Variant
     */
    public final DyeableBlockVariant setLootTable(Function<Block, LootTable> lootTableBuilder){
        this.lootTableBuilder = lootTableBuilder;

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

        //ColorBlockRegistry.registerBlockTypeWithRecursion(this);
        DyeableBlockVariant.ADDITION_BLOCK_VARIANTS.add(this);

        return this;
    }

    //---------------------------------------------------------------------------------------------------

    /**
     * The Common tag based off the {@link #primaryBlockTag} that is made from the {@link #variantIdentifier}
     *
     * @return A Block Tag within fabric's common namespace from the variant used
     */
    public final TagKey<Block> getCommonBlockTag() {
        return TagKey.of(Registry.BLOCK_KEY, new Identifier("c", primaryBlockTag.id().getPath()));
    }

    /**
     * The Common tag based off the {@link #primaryItemTag} that is made from the {@link #variantIdentifier}
     *
     * @return A Item Tag within fabric's common namespace from the variant used
     */
    public final TagKey<Item> getCommonItemTag() {
        return TagKey.of(Registry.ITEM_KEY, new Identifier("c", primaryItemTag.id().getPath()));
    }

    public final boolean vanillaDyesOnly(){
        return this.vanillaDyeableOnly;
    }

    public final boolean alwaysReadOnly(){
        return this.alwaysReadOnly;
    }

    //---------------------------------------------------------------------------------------------------

    /**
     * Checks if the given {@link ItemConvertible}, which can be a block or Item, is a Variant of the Given Variant.
     *
     * @param convertible possible variant
     * @return True if the given Entry is a Variant
     */
    public boolean isSuchAVariant(ItemConvertible convertible) {
        return this.isSuchAVariant(JelloItemSettings.getIdFromConvertible(convertible));
    }

    /**
     * Checks if the given {@link Identifier}, which can be a block or Item id, is a Variant of the Given Variant.
     *
     * @param identifier possible variant identifier
     * @return True if the given Entry is a Variant
     */
    @ApiStatus.Internal
    public boolean isSuchAVariant(Identifier identifier) {
        if(Objects.equals(identifier.getPath(), defaultBlockIdentifier.getPath()))
            return true;

        String[] pathParts = identifier.getPath().split("_");

        if (pathParts.length <= wordCount)
            return false;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = pathParts.length - wordCount; i < pathParts.length; i++) {
            stringBuilder.append(pathParts[i]);

            if (i < pathParts.length - 1) {
                stringBuilder.append("_");
            }
        }

        return stringBuilder.toString().equals(this.variantIdentifier.getPath());
    }

    /**
     * Attempts to check if a Block is Dyeable and if it is will attempt to use the Variant to get the Colored Block passed to it
     *
     * @param block Possibly Colorable Block
     * @param dyeColorant Color being applied to the Block
     * @return A block if the Variant exists and meets certain parameters within the Variant else it returns null
     */
    @Nullable
    public static Block attemptToGetColoredBlock(Block block, DyeColorant dyeColorant){
        DyeableBlockVariant variant = DyeableBlockVariant.getVariantFromBlock(block);

        if(variant != null){
            if(variant.vanillaDyesOnly() && !dyeColorant.isIn(JelloTags.DyeColor.VANILLA_DYES)){
                return null;
            }

            return variant.getColoredBlock(dyeColorant);
        }else{
            return null;
        }
    }

    /**
     * Gets a Block based off the given {@link DyeColorant} and the {@link #variantIdentifier} of the Variant used
     * @param dyeColorant Desired Color or default block if it is {@link DyeColorantRegistry#NULL_VALUE_NEW}
     */
    public Block getColoredBlock(DyeColorant dyeColorant) {
        if(dyeColorant == DyeColorantRegistry.NULL_VALUE_NEW)
            return this.getDefaultBlock();

        String nameSpace = this.variantIdentifier.getNamespace();

        if(!dyeColorant.isIn(JelloTags.DyeColor.VANILLA_DYES)) {
            if (Objects.equals(nameSpace, "minecraft")) {
                nameSpace = dyeColorant.getId().getNamespace();
            }
        }

        return Registry.BLOCK.get(new Identifier(nameSpace, getColoredBlockPath(dyeColorant)));
    }

    /**
     * @return A Block based on the given {@link #defaultBlockIdentifier}.
     */
    public Block getDefaultBlock() {
        return Registry.BLOCK.get(this.defaultBlockIdentifier);
    }

    /**
     * Attempts to get a {@link DyeableBlockVariant} from a given {@link ItemConvertible}
     * @param convertible possible Block or Item of a {@link DyeableBlockVariant}
     * @return {@link DyeableBlockVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    public static DyeableBlockVariant getVariantFromBlock(ItemConvertible convertible){
        return getVariantFromBlock(JelloItemSettings.getIdFromConvertible(convertible));
    }

    /**
     * Attempts to get a {@link DyeableBlockVariant} from a given {@link Identifier}
     * @param identifier possible identifier
     * @return {@link DyeableBlockVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    private static DyeableBlockVariant getVariantFromBlock(Identifier identifier){
        for(DyeableBlockVariant variant : getAllVariants()){
            if(variant.isSuchAVariant(identifier)){
                return variant;
            }
        }

        return null;
    }

    /**
     * Safe way of making sure all variants are added to the main Set that contains all the Registered Block Variants
     * @return {@link #ALL_BLOCK_VARIANTS} safely
     */
    public static Set<DyeableBlockVariant> getAllVariants(){
        if(ALL_BLOCK_VARIANTS.isEmpty() || ALL_BLOCK_VARIANTS.size() < DyedVariantContainer.getVariantMap().get(DyeColorantRegistry.WHITE).dyedBlocks.size()){
            for(DyeableBlockVariant dyeableBlockVariant : VanillaBlockVariants.VANILLA_VARIANTS){
                addToAllBlockVariantsRecursive(dyeableBlockVariant);
            }

            for(DyeableBlockVariant dyeableBlockVariant : ADDITION_BLOCK_VARIANTS){
                addToAllBlockVariantsRecursive(dyeableBlockVariant);
            }
        }

        return ALL_BLOCK_VARIANTS;
    }

    private static void addToAllBlockVariantsRecursive(DyeableBlockVariant dyeableBlockVariant){
        ALL_BLOCK_VARIANTS.add(dyeableBlockVariant);
        if(dyeableBlockVariant.childVariant != null){
            addToAllBlockVariantsRecursive(dyeableBlockVariant.childVariant.get());
        }
    }

    /**
     * Returns a String from the given {@link DyeColorant} and the {@link #variantIdentifier}
     * @param dyeColorant Desired Color
     */
    @ApiStatus.Internal
    public String getColoredBlockPath(DyeColorant dyeColorant) {
        return dyeColorant.getName() + "_" + this.variantIdentifier.getPath();
    }

    /**
     * Attempts to get the color from a possible Variant.
     *
     * @param convertible Possible Block or Item of the given Variant
     * @return The DyeColorant of the entry or null if the given it isn't a given Variant
     */
    @Nullable
    @ApiStatus.Internal
    public DyeColorant getColorFromEntry(ItemConvertible convertible){
        Identifier identifier = JelloItemSettings.getIdFromConvertible(convertible);

        if(!this.isSuchAVariant(identifier))
            return null;

        String[] pathParts = identifier.getPath().split("_");

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pathParts.length - wordCount; i++) {
            stringBuilder.append(pathParts[i]);

            if (i < pathParts.length - wordCount - 1) {
                stringBuilder.append("_");
            }
        }

        return DyeColorantRegistry.DYE_COLOR.get(new Identifier(identifier.getNamespace(), stringBuilder.toString()));
    }

    //---------------------------------------------------------------------------------------------------

    @ApiStatus.Internal
    protected static DyeableBlockVariant of(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, possibleChildVariant, null, blockMaker);
    }

    @ApiStatus.Internal
    protected static DyeableBlockVariant of(Identifier variantIdentifier, BlockMaker blockMaker) {
        return new DyeableBlockVariant(variantIdentifier, null, null, blockMaker);
    }

    protected final void addToTags(Block block, boolean readOnly){
        this.addToBlockTags(block, readOnly);

        if(createBlockItem){
            this.addToItemTags(block.asItem(), readOnly);
        }
    }

    @ApiStatus.Internal
    protected final void addToItemTags(Item item, boolean readOnly) {
        if(item == Blocks.AIR.asItem()){
            return;
        }

        TagInjector.injectItems(JelloTags.Items.ALL_COLORED_VARIANTS.id(), item);
        TagInjector.injectItems(primaryItemTag.id(), item);

        if(!readOnly) {
            for (TagKey<Item> tagKey : secondaryItemTags) {
                TagInjector.injectItems(tagKey.id(), item);
            }
        }

        if(addCustomDefaultBlockToTag && item != this.getDefaultBlock().asItem()){
            this.addToItemTags(this.getDefaultBlock().asItem(), true);
        }
    }

    @ApiStatus.Internal
    protected final void addToBlockTags(Block block, boolean readOnly) {
        TagInjector.injectBlocks(primaryBlockTag.id(), block);

        if(!readOnly) {
            for (TagKey<Block> tagKey : secondaryBlockTags) {
                TagInjector.injectBlocks(tagKey.id(), block);
            }
        }

        if(addCustomDefaultBlockToTag && block != this.getDefaultBlock()) {
            this.addToBlockTags(this.getDefaultBlock(), true);
        }
    }

    public void generateAllLootTables(Map<Identifier, LootTable> tables){
        if(alwaysReadOnly){
            return;
        }

        Set<DyeColorant> dyeColorants = new HashSet<>();

        if(vanillaDyeableOnly){
            dyeColorants.addAll(DyeColorantRegistry.Constants.VANILLA_DYES);
        }else{
            dyeColorants.addAll(DyeColorantRegistry.DYE_COLOR.stream().toList());
        }

        for(DyeColorant dyeColorant : dyeColorants){
            Block block = this.getColoredBlock(dyeColorant);

            tables.put(block.getLootTableId(), this.lootTableBuilder.apply(block));
        }
    }

    @ApiStatus.Internal
    protected RegistryInfo makeChildBlock(DyeColorant dyeColorant, @Nullable Block parentBlock) {
        return RegistryInfo.of(blockMaker.createBlockFromDyeColor(dyeColorant, parentBlock), defaultSettings);
    }

    @ApiStatus.Internal
    protected BlockItem makeBlockItem(DyeColorant dyeColorant, Block block, Item.Settings settings) {
        return this.blockItemMaker.createBlockItemFromDyeColor(dyeColorant, block, settings);
    }

    //---------------------------------------------------------------------------------------------------

    protected static class BlockItemSettings {
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
        private final BlockItemSettings settings;

        protected OwoItemSettings overrideSettings = null;

        protected RegistryInfo(Block block, BlockItemSettings blockItemSettings) {
            this.block = block;
            this.settings = blockItemSettings;
        }

        protected static RegistryInfo of(Block block, @Nullable BlockItemSettings blockItemSettings) {
            return new RegistryInfo(block, blockItemSettings);
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
