package io.wispforest.jello.api.dye.registry.variants.block;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.ColorManipulators;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableVariant;
import io.wispforest.jello.api.dye.registry.variants.DyeableVariantManager;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.api.dye.registry.variants.item.DyeableItemVariant;
import io.wispforest.jello.api.dye.registry.variants.item.ItemMaker;
import io.wispforest.jello.api.events.LootTableInjectionEvent;
import io.wispforest.jello.api.util.VersatileLogger;
import io.wispforest.jello.data.loot.JelloLootTables;
import io.wispforest.jello.data.tags.JelloTags;
import io.wispforest.jello.api.item.JelloItemSettings;
import io.wispforest.jello.misc.dye.JelloBlockVariants;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.Block;
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
import java.util.stream.Collectors;

/**
 * A {@link DyeableBlockVariant} is a way to add your own
 * Dyed Block Variants, like Minecraft's Wool and Concrete,
 * to Jello's System so that any {@link DyeColorant}
 * created gets made with your Variant.
 */
public class DyeableBlockVariant extends DyeableVariant<DyeableBlockVariant, Block> {

    private @Nullable final BlockMaker blockMaker;
    public final DyeableItemVariant blockItemVariant;

    protected final List<TagKey<Block>> allBlockTags = new ArrayList<>();

    protected Function<Block, LootTable> lootTableBuilder = (itemConvertible) -> JelloLootTables.drops(itemConvertible).build();

    protected ColorManipulators.AlterBlockColor colorChangeMethod = ColorManipulators.AlterBlockColor.DEFAULT;

    /**
     * @param variantIdentifier The {@link Identifier} based off your Modid and the block path for your variant
     * @param possibleChildVariant Any Variant that needs this Block to create itself from
     * @param itemSettings The {@link Item.Settings} used when creating the blockItem
     * @param blockMaker A generalized way of creating your Block Variant (Look at {@link VanillaBlockVariants} or {@link JelloBlockVariants} for a example)
     */
    public DyeableBlockVariant(Identifier variantIdentifier, @Nullable Supplier<DyeableBlockVariant> possibleChildVariant, Item.Settings itemSettings, @Nullable BlockMaker blockMaker) {
        super(variantIdentifier, possibleChildVariant);

        this.blockItemVariant = new DyeableItemVariant(variantIdentifier, null, itemSettings, ItemMaker.BLOCK_DEFAULT);
        this.blockItemVariant.itemColorChangeMethod = ColorManipulators.AlterItemColor.DEFAULT_BLOCK;

        this.blockMaker = blockMaker;

        this.defaultEntryIdentifier = new Identifier(variantIdentifier.getNamespace(), "white_" + variantIdentifier.getPath());

        allBlockTags.add(TagKey.of(Registry.BLOCK_KEY, Jello.id(variantIdentifier.getPath())));
    }

    //---------------------------------------------------------------------------------------------------

    public static class Builder {

        DyeableBlockVariant blockVariant;

        Builder(DyeableBlockVariant variant){
            this.blockVariant = variant;
        }

        public static Builder of(Identifier variantIdentifier, Item.Settings blockItemSettings, Supplier<DyeableBlockVariant> possibleChildVariant, BlockMaker blockMaker) {
            return new Builder(new DyeableBlockVariant(variantIdentifier, possibleChildVariant, blockItemSettings, blockMaker));
        }

        public static Builder of(Identifier variantIdentifier, Item.Settings blockItemSettings, BlockMaker blockMaker) {
            return new Builder(new DyeableBlockVariant(variantIdentifier, null, blockItemSettings, blockMaker));
        }

        /**
         * A way of using the Coloring Events within Jello with only Vanilla Colors and Blocks added by your Mod
         *
         * @param variantIdentifier The {@link Identifier} based off your Modid and the block path for your variant
         */
        public static Builder readOnly(Identifier variantIdentifier) {
            Builder builder = new Builder(new DyeableBlockVariant(variantIdentifier, null, new Item.Settings(), null));

            builder.setVanillaDyeableOnly();

            return builder;
        }

        public static Builder readOnly(Identifier variantIdentifier, Supplier<DyeableBlockVariant> possibleChildVariant) {
            Builder builder = new Builder(new DyeableBlockVariant(variantIdentifier, possibleChildVariant, new Item.Settings(), null));

            builder.setVanillaDyeableOnly();

            return builder;
        }

        //--------------------------------------------------------------------------------------------------------------

        /**
         * Disables creation of BlockItem for the given variant
         */
        public final Builder noBlockItem(){
            blockVariant.blockItemVariant.itemMaker = null;
            blockVariant.blockItemVariant.defaultItemSettings = new Item.Settings();

            return this;
        }

        /**
         * Manually change the {@link DyeableItemVariant#itemMaker} if a custom one is needed
         *
         * @param blockItemMaker Custom BlockItemMaker
         */
        public final Builder setBlockItemMaker(ItemMaker blockItemMaker) {
            blockVariant.blockItemVariant.itemMaker = blockItemMaker;

            return this;
        }

        /**
         * Add all tags needed for this Block to be added too.
         * You will need at least one Tag which this block variant is linked too or the {@link #addToBlockTags} will throw a {@link NullPointerException}
         *
         * @param tags Tags to be added to when the block is built
         */
        @SafeVarargs
        public final Builder setBlockTags(TagKey<Block>... tags) {
            blockVariant.allBlockTags.addAll(Arrays.asList(tags));

            return this;
        }

        /**
         * Add all tags needed for the Created {@link Item} if such is made
         *
         * @param tags Tags to be added to when the {@link Item} is built
         */
        @SafeVarargs
        public final Builder setItemTags(TagKey<Item>... tags) {
            blockVariant.blockItemVariant.allItemTags.addAll(Arrays.asList(tags));

            return this;
        }

        /**
         * This method sets the {@link ColorManipulators.AlterBlockColor} for this variant, which is used
         * internally for when a player is going to Dye a Colorable block of this Variant using a DyeItem or such.
         * <br><br>
         * Allows for blocks that are two parts like bed or BlockEntity's that need special things like NBT data to be copyied to change states properly
         */
        public final Builder setBlockStateChangeMethod(ColorManipulators.AlterBlockColor method){
            blockVariant.colorChangeMethod = method;

            return this;
        }

        /**
         * Change the default Function for automatically generating the {@link LootTable} for this Block Variant
         */
        public final Builder setLootTable(Function<Block, LootTable> lootTableBuilder){
            blockVariant.lootTableBuilder = lootTableBuilder;

            return this;
        }

        /**
         * Manually change the {@link #defaultEntryIdentifier} Identifier
         *
         * @param identifier The identifier of the block
         */
        public final Builder setDefaultEntry(Identifier identifier) {
            blockVariant.defaultEntryIdentifier = identifier;
            blockVariant.blockItemVariant.defaultEntryIdentifier = identifier;

            return this;
        }

        /**
         * Manually change the {@link #defaultEntryIdentifier} Identifier by combining the Block's path and the variant's MODID
         *
         * @param path The Block's default path
         */
        public final Builder setDefaultEntry(String path) {
            return this.setDefaultEntry(new Identifier(blockVariant.variantIdentifier.getNamespace(), path));
        }

        /**
         * Disables the creation of Modded Dyed Variants and only allows for Coloring this block with Vanilla Colors
         */
        public final Builder setVanillaDyeableOnly(){
            blockVariant.vanillaColorsOnly = true;

            return this;
        }

        public final Builder setParentVariantIdentifier(Identifier identifier){
            blockVariant.parentVariantIdentifier = identifier;

            return this;
        }

        //--------------------------------------------------------------------------------------------------------------

        /**
         * Method must be called when the Variant is finished being edited
         * Will add your variant to the {@link #REGISTERED_BLOCK_VARIANTS} and
         * retroactively add this {@link DyeableBlockVariant} and {@link DyeableVariantManager#updateExistingDataForItem}
         */
        public final DyeableBlockVariant register() {
            if (!DyeableBlockVariant.REGISTERED_BLOCK_VARIANTS.contains(blockVariant)) {
                if(blockVariant.parentVariantIdentifier == null) {
                    DyeableVariantManager.updateExistingDataForBlock(blockVariant);
                }

                DyeableBlockVariant.REGISTERED_BLOCK_VARIANTS.add(blockVariant);
            }

            if(blockVariant.createBlockItem()){
                DyeableBlockVariant.REGISTERED_BLOCK_ITEM_VARIANTS.add(blockVariant.blockItemVariant);
            }

            return blockVariant;
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
     * The primary block tag that groups all these blocks together
     */
    public final TagKey<Block> getPrimaryBlockTag() {
        return allBlockTags.get(0);
    }

    /**
     * If the given Variant is only trying to use Jello's API rather than creating entry's
     */
    public boolean alwaysReadOnly(){
        return this.blockMaker == null;
    }

    //---------------------------------------------------------------------------------------------------

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

            return new Pair<>(variant.getColoredEntry(dyeColorant), variant);
        } else {
            return null;
        }
    }

    /**
     * Gets an Entry based off the given {@link DyeColorant} and the {@link #variantIdentifier} of the Variant used
     * @param dyeColorant Desired Color or default entry if it is {@link DyeColorantRegistry#NULL_VALUE_NEW}
     */
    @Override
    public Block getColoredEntry(DyeColorant dyeColorant) {
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

    @Override
    protected Block getEntryFromIdentifier(Identifier identifier) {
        return Registry.BLOCK.get(identifier);
    }

    @Override
    protected TagKey<Block> getPrimaryTag() {
        return this.getPrimaryBlockTag();
    }

    /**
     * @return A Block based on the given {@link #defaultEntryIdentifier}.
     */
    public Block getDefaultBlock() {
        return Registry.BLOCK.get(this.defaultEntryIdentifier);
    }

    /**
     * Gets a Block Item based off the given {@link DyeColorant} and the {@link #variantIdentifier} of the Variant used
     * @param dyeColorant Desired Color or default blockItem if it is {@link DyeColorantRegistry#NULL_VALUE_NEW}
     */
    public Item getColoredBlockItem(DyeColorant dyeColorant) {
        return blockItemVariant.getColoredEntry(dyeColorant);
    }

    /**
     * @return A Block Item based on the given {@link #defaultEntryIdentifier}.
     */
    public Item getDefaultBlockItem() {
        return blockItemVariant.getDefaultItem();
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
        for(DyeableBlockVariant variant : getAllBlockVariants()){
            if(variant.isSuchAVariant(identifier, true)){
                return variant;
            }
        }

        return null;
    }

    /**
     * Safe way of making sure all variants are added to the main Set that contains all the Registered Block Variants
     * @return {@link #REGISTERED_BLOCK_VARIANTS} safely
     */
    public static Set<DyeableBlockVariant> getAllBlockVariants(){
        return REGISTERED_BLOCK_VARIANTS;
    }

    public static Set<DyeableBlockVariant> AllBaseBlockVariants(){
        return REGISTERED_BLOCK_VARIANTS.stream().filter(dyeableItemVariant -> dyeableItemVariant.parentVariantIdentifier == null).collect(Collectors.toSet());
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

        if(!this.isSuchAVariant(identifier, true))
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
        return blockItemVariant.itemMaker != null;
    }

    @ApiStatus.Internal
    public final void addToTags(Block block, boolean readOnly){
        this.addToBlockTags(block, readOnly);

        if(createBlockItem()){
            this.blockItemVariant.addToTags(block.asItem(), readOnly);
        }
    }

    private boolean initilizedDefaultBlocksBlockTag = false;

    @ApiStatus.Internal
    protected final void addToBlockTags(Block block, boolean readOnly) {
        TagInjector.inject(Registry.BLOCK, getPrimaryBlockTag().id(), block);

        if(!readOnly) {
            for (TagKey<Block> tagKey : allBlockTags.subList(1, allBlockTags.size())) {
                TagInjector.inject(Registry.BLOCK, tagKey.id(), block);
            }
        }

        if(defaultEntryIdentifier.getPath().contains("white") && block != this.getDefaultBlock() && !initilizedDefaultBlocksBlockTag) {
            this.addToBlockTags(this.getDefaultBlock(), true);
            initilizedDefaultBlocksBlockTag = true;
        }
    }

    @ApiStatus.Internal
    public void generateAllLootTables(LootTableInjectionEvent.LootTableMapHelper helper){
        if(alwaysReadOnly() || !createBlockItem())
            return;

        Set<DyeColorant> dyeColorants = new HashSet<>();

        if(vanillaColorsOnly){
            dyeColorants.addAll(DyeColorantRegistry.Constants.VANILLA_DYES);
        } else {
            dyeColorants.addAll(DyeColorantRegistry.DYE_COLOR.stream().toList());
        }

        VersatileLogger logger = new VersatileLogger("DyeableBlockVariantLootTables", () -> Jello.DEBUG_ENV_VAR | Jello.DEBUG_ENV);

        for(DyeColorant dyeColorant : dyeColorants){
            Block block = this.getColoredEntry(dyeColorant);

            Identifier blockId = Registry.BLOCK.getId(block);

            if(Objects.equals(blockId.getNamespace(), "minecraft") || (Objects.equals(this.defaultEntryIdentifier.getPath(), blockId.getPath()) && this.customDefaultBlock()))
                continue;

            if(!helper.addLootTable(block.getLootTableId(), this.lootTableBuilder.apply(block))){
                logger.failMessage("Seems that a lootTable for a block [" + "lootTableId: " + block.getLootTableId() + ", " + "colorId: " + dyeColorant.toString() + ", " + "blockId: " + block.toString() + ", " + "variantId: " + this.toString() + "] already exists, will not be added then");
            }
        }
    }

    @ApiStatus.Internal
    public Pair<Block, Item.Settings> makeChildBlock(DyeColorant dyeColorant, @Nullable Block parentBlock) {
        return new Pair<>(blockMaker.createBlockFromDyeColor(dyeColorant, parentBlock), this.blockItemVariant.defaultItemSettings);
    }

    @ApiStatus.Internal
    public BlockItem makeBlockItem(DyeColorant dyeColorant, Block block, Item.Settings settings) {
        return (BlockItem) this.blockItemVariant.makeItem(dyeColorant, block, settings);
    }

    @Override
    public String toString() {
        return this.variantIdentifier.toString();
    }
}
