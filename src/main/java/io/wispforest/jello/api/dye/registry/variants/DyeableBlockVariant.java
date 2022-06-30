package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.ColorManipulators;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.data.loot.JelloLootTables;
import io.wispforest.jello.data.tags.JelloTags;
import io.wispforest.jello.api.item.JelloItemSettings;
import io.wispforest.jello.misc.dye.JelloBlockVariants;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
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

    private static final Set<DyeableBlockVariant> ALL_BLOCK_VARIANTS = new LinkedHashSet<>();
    private static final Set<DyeableBlockVariant> REGISTERED_BLOCK_VARIANTS = new LinkedHashSet<>();

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

    @Nullable private final BlockMaker blockMaker;

    private Identifier defaultBlockIdentifier;

    private BlockItemMaker blockItemMaker;
    private Item.Settings defaultBlockItemSettings;

    private boolean vanillaColorsOnly = false;

    private final List<TagKey<Block>> allBlockTags = new ArrayList<>();
    private final List<TagKey<Item>> allItemTags = new ArrayList<>();

    public RecursiveType recursiveType = RecursiveType.NONE;
    public Supplier<DyeableBlockVariant> childVariant = () -> null;

    private Function<Block, LootTable> lootTableBuilder = (block) -> JelloLootTables.drops(block).build();

    private ColorManipulators.AlterBlockColor colorChangeMethod = ColorManipulators.AlterBlockColor.DEFAULT;

    /**
     * @param variantIdentifier The {@link Identifier} based off your Modid and the block path for your variant
     * @param possibleChildVariant Any Variant that needs this Block to create itself from
     * @param itemSettings The {@link Item.Settings} used when creating the blockItem
     * @param blockMaker A generalized way of creating your Block Variant (Look at {@link VanillaBlockVariants} or {@link JelloBlockVariants} for a example)
     */
    public DyeableBlockVariant(Identifier variantIdentifier, @Nullable Supplier<DyeableBlockVariant> possibleChildVariant, Item.Settings itemSettings, @Nullable BlockMaker blockMaker) {
        this.variantIdentifier = variantIdentifier;
        this.blockMaker = blockMaker;

        this.wordCount = variantIdentifier.getPath().split("_").length;

        this.defaultBlockItemSettings = itemSettings;

        if (possibleChildVariant != null) {
            this.recursiveType = RecursiveType.CHAINED;
            this.childVariant = possibleChildVariant;
        }

        this.defaultBlockIdentifier = new Identifier(variantIdentifier.getNamespace(), "white_" + variantIdentifier.getPath());
        this.blockItemMaker = BlockItemMaker.DEFAULT;

        allBlockTags.add(TagKey.of(Registry.BLOCK_KEY, Jello.id(variantIdentifier.getPath())));
        allItemTags.add(TagKey.of(Registry.ITEM_KEY, Jello.id(variantIdentifier.getPath())));
    }

    //---------------------------------------------------------------------------------------------------

    public static class Builder {

        public DyeableBlockVariant variant;

        Builder(DyeableBlockVariant variant){
            this.variant = variant;
        }

        public static DyeableBlockVariant.Builder of(Identifier variantIdentifier, Item.Settings blockItemSettings, Supplier<DyeableBlockVariant> possibleChildVariant, BlockMaker blockMaker) {
            return new Builder(new DyeableBlockVariant(variantIdentifier, possibleChildVariant, blockItemSettings, blockMaker));
        }

        public static DyeableBlockVariant.Builder of(Identifier variantIdentifier, Item.Settings blockItemSettings, BlockMaker blockMaker) {
            return new Builder(new DyeableBlockVariant(variantIdentifier, null, blockItemSettings, blockMaker));
        }

        /**
         * A way of using the Coloring Events within Jello with only Vanilla Colors and Blocks added by your Mod
         *
         * @param variantIdentifier The {@link Identifier} based off your Modid and the block path for your variant
         */
        public static DyeableBlockVariant.Builder readOnly(Identifier variantIdentifier) {
            DyeableBlockVariant.Builder builder = new Builder(new DyeableBlockVariant(variantIdentifier, null, new Item.Settings(), null));

            builder.setVanillaDyeableOnly();

            return builder;
        }

        public static DyeableBlockVariant.Builder readOnly(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant) {
            DyeableBlockVariant.Builder builder = new Builder(new DyeableBlockVariant(variantIdentifier, possibleChildVariant, new Item.Settings(), null));

            builder.setVanillaDyeableOnly();

            return builder;
        }

        //---------------------------------------------------------------------------------------------------

        /**
         * Manually change the {@link #defaultBlockIdentifier} Identifier
         *
         * @param identifier The identifier of the block
         */
        public final DyeableBlockVariant.Builder setDefaultBlock(Identifier identifier) {
            variant.defaultBlockIdentifier = identifier;

            return this;
        }

        /**
         * Manually change the {@link #defaultBlockIdentifier} Identifier by combining the Block's path and the variant's MODID
         *
         * @param path The Block's default path
         */
        public final DyeableBlockVariant.Builder setDefaultBlock(String path) {
            return this.setDefaultBlock(new Identifier(variant.variantIdentifier.getNamespace(), path));
        }

        /**
         * Disables creation of BlockItem for the given variant
         */
        public final DyeableBlockVariant.Builder noBlockItem(){
            variant.blockItemMaker = null;
            variant.defaultBlockItemSettings = new Item.Settings();

            return this;
        }

        /**
         * Manually change the {@link #blockItemMaker} if a custom one is needed
         *
         * @param blockItemMaker Custom BlockItemMaker
         */
        public final DyeableBlockVariant.Builder setBlockItemMaker(BlockItemMaker blockItemMaker) {
            variant.blockItemMaker = blockItemMaker;

            return this;
        }

        /**
         * Add all tags needed for this Block to be added too.
         * You will need at least one Tag which this block variant is linked too or the {@link #addToBlockTags} will throw a {@link NullPointerException}
         *
         * @param tags Tags to be added to when the block is built
         */
        @SafeVarargs
        public final DyeableBlockVariant.Builder setBlockTags(TagKey<Block>... tags) {
            variant.allBlockTags.addAll(Arrays.asList(tags));

            return this;
        }

        /**
         * Add all tags needed for the Created {@link BlockItem} if such is made
         *
         * @param tags Tags to be added to when the {@link BlockItem} is built
         */
        @SafeVarargs
        public final DyeableBlockVariant.Builder setItemTags(TagKey<Item>... tags) {
            variant.allItemTags.addAll(Arrays.asList(tags));

            return this;
        }

        /**
         * Disables the creation of Modded Dyed Variants and only allows for Coloring this block with Vanilla Colors
         */
        public final DyeableBlockVariant.Builder setVanillaDyeableOnly(){
            variant.vanillaColorsOnly = true;

            return this;
        }

        /**
         * Change the default Function for automatically generating the {@link LootTable} for this Block Variant
         */
        public final DyeableBlockVariant.Builder setLootTable(Function<Block, LootTable> lootTableBuilder){
            variant.lootTableBuilder = lootTableBuilder;

            return this;
        }

        /**
         * This method sets the {@link ColorManipulators.AlterBlockColor} for this variant, which is used
         * internally for when a player is going to Dye a Colorable block of this Variant using a DyeItem or such.
         * <br><br>
         * Allows for blocks that are two parts like bed or BlockEntity's that need special things like NBT data to be copyied to change states properly
         */
        public final DyeableBlockVariant.Builder setBlockStateChangeMethod(ColorManipulators.AlterBlockColor method){
            variant.colorChangeMethod = method;

            return this;
        }

        /**
         * Method must be called when the Variant is finished being edited
         * Will add your variant to the {@link #REGISTERED_BLOCK_VARIANTS} and
         * retroactively add this {@link DyeableBlockVariant} and {@link DyedVariantContainer#updateExistingContainers}
         */
        public final DyeableBlockVariant register() {
            if (!DyeableBlockVariant.REGISTERED_BLOCK_VARIANTS.contains(variant)) {
                DyedVariantContainer.updateExistingContainers(variant);
            }

            //ColorBlockRegistry.registerBlockTypeWithRecursion(this);
            DyeableBlockVariant.REGISTERED_BLOCK_VARIANTS.add(variant);

            return variant;
        }

    }

    //---------------------------------------------------------------------------------------------------

    /**
     * The Common tag based off the {@link #getPrimaryBlockTag()} that is made from the {@link #variantIdentifier}
     *
     * @return A Block Tag within fabric's common namespace from the variant used
     */
    public final TagKey<Block> getCommonBlockTag() {
        return TagKey.of(Registry.BLOCK_KEY, new Identifier("c", getPrimaryBlockTag().id().getPath()));
    }

    /**
     * The Common tag based off the {@link #getPrimaryItemTag()} that is made from the {@link #variantIdentifier}
     *
     * @return A Item Tag within fabric's common namespace from the variant used
     */
    public final TagKey<Item> getCommonItemTag() {
        return TagKey.of(Registry.ITEM_KEY, new Identifier("c", getPrimaryItemTag().id().getPath()));
    }

    /**
     * The primary block tag that groups all these blocks together
     */
    public final TagKey<Block> getPrimaryBlockTag() {
        return allBlockTags.get(0);
    }

    /**
     * The primary item tag that groups all these blockItems together (If such were made);
     */
    public final TagKey<Item> getPrimaryItemTag() {
        return allItemTags.get(0);
    }

    public final boolean vanillaDyesOnly(){
        return this.vanillaColorsOnly;
    }

    public final boolean alwaysReadOnly(){
        return this.blockMaker == null;
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

    @Nullable
    public static Pair<Block, DyeableBlockVariant> attemptToGetColoredBlockPair(Block block, DyeColorant dyeColorant){
        DyeableBlockVariant variant = DyeableBlockVariant.getVariantFromBlock(block);

        if(variant != null){
            if(!block.getRegistryEntry().isIn(variant.getPrimaryBlockTag())){
                return null;
            }

            DyeColorant blockCurrentColor = variant.getColorFromEntry(block);

            if(blockCurrentColor == dyeColorant || (variant.vanillaDyesOnly() && !dyeColorant.isIn(JelloTags.DyeColor.VANILLA_DYES))){
                return null;
            }

            return new Pair(variant.getColoredBlock(dyeColorant), variant);
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
        //TODO: Is such really needed?

        if(ALL_BLOCK_VARIANTS.isEmpty() || ALL_BLOCK_VARIANTS.size() < DyedVariantContainer.getVariantMap().get(DyeColorantRegistry.WHITE).dyedBlocks.size()){
            for(DyeableBlockVariant dyeableBlockVariant : REGISTERED_BLOCK_VARIANTS){
                addToAllBlockVariantsRecursive(dyeableBlockVariant);
            }
        }

        return ALL_BLOCK_VARIANTS;
    }

    private static void addToAllBlockVariantsRecursive(DyeableBlockVariant dyeableBlockVariant){
        ALL_BLOCK_VARIANTS.add(dyeableBlockVariant);
        if(dyeableBlockVariant.childVariant.get() != null){
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

    public ColorManipulators.AlterBlockColor getAlterColorMethod(){
        return this.colorChangeMethod;
    }

    //---------------------------------------------------------------------------------------------------

    @ApiStatus.Internal
    public boolean createBlockItem(){
        return blockItemMaker != null;
    }

    private boolean addCustomDefaultBlockToTag(){
        return defaultBlockIdentifier.getPath().contains("white");
    }

    protected final void addToTags(Block block, boolean readOnly){
        this.addToBlockTags(block, readOnly);

        if(createBlockItem()){
            this.addToItemTags(block.asItem(), readOnly);
        }
    }

    private boolean initilizedDefaultBlocksItemTag = false;

    @ApiStatus.Internal
    protected final void addToItemTags(Item item, boolean readOnly) {
        if(item == Blocks.AIR.asItem()){
            return;
        }

        TagInjector.inject(Registry.ITEM, JelloTags.Items.ALL_COLORED_VARIANTS.id(), item);
        TagInjector.inject(Registry.ITEM, getPrimaryItemTag().id(), item);

        if(!readOnly) {
            for (TagKey<Item> tagKey : allItemTags.subList(1, allItemTags.size())) {
                TagInjector.inject(Registry.ITEM, tagKey.id(), item);
            }
        }

        if(addCustomDefaultBlockToTag() && item != this.getDefaultBlock().asItem() && !initilizedDefaultBlocksItemTag){
            this.addToItemTags(this.getDefaultBlock().asItem(), true);
            initilizedDefaultBlocksItemTag = true;
        }
    }

    private boolean initilizedDefaultBlocksBlockTag = false;

    @ApiStatus.Internal
    protected final void addToBlockTags(Block block, boolean readOnly) {
        TagInjector.inject(Registry.BLOCK, getPrimaryItemTag().id(), block);

        if(!readOnly) {
            for (TagKey<Block> tagKey : allBlockTags.subList(1, allItemTags.size())) {
                TagInjector.inject(Registry.BLOCK, tagKey.id(), block);
            }
        }

        if(addCustomDefaultBlockToTag() && block != this.getDefaultBlock() && !initilizedDefaultBlocksBlockTag) {
            this.addToBlockTags(this.getDefaultBlock(), true);
            initilizedDefaultBlocksBlockTag = true;
        }
    }

    @ApiStatus.Internal
    public void generateAllLootTables(Map<Identifier, LootTable> tables){
        if(alwaysReadOnly()){
            return;
        }

        Set<DyeColorant> dyeColorants = new HashSet<>();

        if(vanillaColorsOnly){
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
        return new RegistryInfo(blockMaker.createBlockFromDyeColor(dyeColorant, parentBlock), defaultBlockItemSettings);
    }

    @ApiStatus.Internal
    protected BlockItem makeBlockItem(DyeColorant dyeColorant, Block block, Item.Settings settings) {
        return this.blockItemMaker.createBlockItemFromDyeColor(dyeColorant, block, settings);
    }

    //---------------------------------------------------------------------------------------------------

    @ApiStatus.Internal
    protected static class RegistryInfo {

        public final Block block;
        private final Item.Settings settings;

        protected RegistryInfo(Block block, Item.Settings blockItemSettings) {
            this.block = block;
            this.settings = blockItemSettings;
        }

        protected Item.Settings getItemSettings() {
            return JelloItemSettings.copyFrom(settings);
        }
    }
}
