package io.wispforest.gelatin.dye_entries.variants.item;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.common.util.BetterItemOps;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_entries.BlockColorManipulators;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariant;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantManager;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_registry.data.GelatinTags;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A {@link DyeableItemVariant} is a way to add your own
 * Dyed Item Variants to Jello's System so that
 * any {@link DyeColorant} created gets made with your Variant.
 */
public class DyeableItemVariant extends DyeableVariant<DyeableItemVariant, Item> {

    public @Nullable
    ItemMaker itemMaker;
    public Item.Settings defaultItemSettings;

    public final List<TagKey<Item>> allItemTags = new ArrayList<>();

    public BlockColorManipulators.AlterItemColor itemColorChangeMethod = BlockColorManipulators.AlterItemColor.DEFAULT_ITEM;

    /**
     * @param variantIdentifier The {@link Identifier} based off your Modid and the block path for your variant
     * @param possibleChildVariant Any Variant that needs this Item to create itself from
     * @param itemSettings The {@link Item.Settings} used when creating the blockItem
     * @param itemMaker A generalized way of creating your Item Variant
     */
    public DyeableItemVariant(Identifier variantIdentifier, @Nullable Supplier<DyeableItemVariant> possibleChildVariant, Item.Settings itemSettings, @Nullable ItemMaker itemMaker) {
        super(variantIdentifier, possibleChildVariant);

        this.itemMaker = itemMaker;
        this.defaultItemSettings = itemSettings;

        this.defaultEntryIdentifier = new Identifier(variantIdentifier.getNamespace(), "white_" + variantIdentifier.getPath());

        allItemTags.add(TagKey.of(Registry.ITEM_KEY, GelatinConstants.id(variantIdentifier.getPath())));
    }

    public static class Builder {

        DyeableItemVariant itemVariant;

        public Builder(DyeableItemVariant variant){
            this.itemVariant = variant;
        }

        public static Builder of(Identifier variantIdentifier, Item.Settings blockItemSettings, Supplier<DyeableItemVariant> possibleChildVariant, ItemMaker blockMaker) {
            return new Builder(new DyeableItemVariant(variantIdentifier, possibleChildVariant, blockItemSettings, blockMaker));
        }

        public static Builder of(Identifier variantIdentifier, Item.Settings blockItemSettings, ItemMaker blockMaker) {
            return new Builder(new DyeableItemVariant(variantIdentifier, null, blockItemSettings, blockMaker));
        }

        /**
         * A way of using the Coloring Events within Jello with only Vanilla Colors and Items added by your Mod
         *
         * @param variantIdentifier The {@link Identifier} based off your Modid and the item path for your variant
         */
        public static Builder readOnly(Identifier variantIdentifier) {
            Builder builder = new Builder(new DyeableItemVariant(variantIdentifier, null, new Item.Settings(), null));

            builder.setVanillaDyeableOnly();

            return builder;
        }

        public static Builder readOnly(Identifier variantIdentifier, Supplier<DyeableItemVariant> possibleChildVariant) {
            Builder builder = new Builder(new DyeableItemVariant(variantIdentifier, possibleChildVariant, new Item.Settings(), null));

            builder.setVanillaDyeableOnly();

            return builder;
        }

        /**
         * Manually change the {@link #defaultEntryIdentifier} Identifier
         *
         * @param identifier The identifier of the item
         */
        public final Builder setDefaultEntry(Identifier identifier) {
            itemVariant.defaultEntryIdentifier = identifier;

            return this;
        }

        /**
         * Manually change the {@link #defaultEntryIdentifier} Identifier by combining the Item's path and the variant's MODID
         *
         * @param path The Item's default path
         */
        public final Builder setDefaultEntry(String path) {
            return this.setDefaultEntry(new Identifier(itemVariant.variantIdentifier.getNamespace(), path));
        }

        /**
         * Add all tags needed for the Created {@link Item} if such is made
         *
         * @param tags Tags to be added to when the {@link Item} is built
         */
        @SuppressWarnings("unchecked")
        public final Builder setItemTags(TagKey<Item>... tags) {
            itemVariant.allItemTags.addAll(Arrays.asList(tags));

            return this;
        }

        /**
         * Disables the creation of Modded Dyed Variants and only allows for Coloring this item with Vanilla Colors
         */
        public final Builder setVanillaDyeableOnly(){
            itemVariant.vanillaColorsOnly = true;

            return this;
        }

        public final Builder setParentVariantIdentifier(Identifier identifier){
            itemVariant.parentVariantIdentifier = identifier;

            return this;
        }

        /**
         * Method must be called when the Variant is finished being edited
         * Will add your variant to the {@link #REGISTERED_ITEM_VARIANTS} and
         * retroactively add this {@link DyeableItemVariant} and {@link DyeableVariantManager#updateExistingDataForItem}
         */
        public DyeableItemVariant register() {
            if (!DyeableItemVariant.REGISTERED_ITEM_VARIANTS.contains(itemVariant)) {
                if(itemVariant.parentVariantIdentifier == null) {
                    DyeableVariantManager.updateExistingDataForItem(itemVariant);
                }
            }

            //ColorBlockRegistry.registerBlockTypeWithRecursion(this);
            DyeableItemVariant.REGISTERED_ITEM_VARIANTS.add(itemVariant);

            return itemVariant;
        }
    }

    //-----------------------------------------------------------

    /**
     * The Common tag based off the {@link #getPrimaryItemTag()} that is made from the {@link #variantIdentifier}
     *
     * @return A Item Tag within fabric's common namespace from the variant used
     */
    public final TagKey<Item> getCommonItemTag() {
        return TagKey.of(Registry.ITEM_KEY, new Identifier("c", getPrimaryItemTag().id().getPath()));
    }

    /**
     * The primary item tag that groups all these Items together (If such were made);
     */
    public final TagKey<Item> getPrimaryItemTag() {
        return allItemTags.get(0);
    }

    /**
     * If the given Variant is only trying to use Jello's API rather than creating entry's
     */
    public boolean alwaysReadOnly(){
        return this.itemMaker == null;
    }

    //---------------------------------------------------------------------------------------------------

    /**
     * Gets an Entry based off the given {@link DyeColorant} and the {@link #variantIdentifier} of the Variant used
     * @param dyeColorant Desired Color or default entry if it is {@link DyeColorantRegistry#NULL_VALUE_NEW}
     */
    public Item getColoredEntry(DyeColorant dyeColorant) {
        if(dyeColorant == DyeColorantRegistry.NULL_VALUE_NEW)
            return this.getDefaultItem();

        String nameSpace = this.variantIdentifier.getNamespace();

        if(!dyeColorant.isIn(GelatinTags.DyeColor.VANILLA_DYES)) {
            if (Objects.equals(nameSpace, "minecraft")) {
                nameSpace = dyeColorant.getId().getNamespace();
            }
        }

        return Registry.ITEM.get(new Identifier(nameSpace, getColoredEntryPath(dyeColorant)));
    }

    @Override
    protected Item getEntryFromIdentifier(Identifier identifier) {
        return Registry.ITEM.get(identifier);
    }

    @Override
    protected TagKey<Item> getPrimaryTag() {
        return this.getPrimaryItemTag();
    }

    /**
     * @return An Item based on the given {@link #defaultEntryIdentifier}.
     */
    public Item getDefaultItem() {
        return Registry.ITEM.get(this.defaultEntryIdentifier);
    }

    /**
     * Attempts to get a {@link DyeableBlockVariant} from a given {@link ItemConvertible}
     * @param convertible possible Block or Item of a {@link DyeableBlockVariant}
     * @return {@link DyeableVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    public static DyeableItemVariant getVariantFromItem(ItemConvertible convertible){
        return getVariantFromItem(BetterItemOps.getIdFromConvertible(convertible));
    }

    /**
     * Attempts to get a {@link DyeableItemVariant} from a given {@link Identifier}
     * @param identifier possible identifier
     * @return {@link DyeableItemVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    private static DyeableItemVariant getVariantFromItem(Identifier identifier){
        for(DyeableItemVariant variant : getAllItemVariants()){
            if(variant.isSuchAVariant(identifier, true)){
                return variant;
            }
        }

        return null;
    }

    /**
     * Safe way of making sure all variants are added to the main Set that contains all the Registered Item Variants
     * @return {@link #REGISTERED_ITEM_VARIANTS} safely
     */
    public static Set<DyeableItemVariant> getAllItemVariants(){
        return REGISTERED_ITEM_VARIANTS;
    }

    public static Set<DyeableItemVariant> AllBaseItemVariants(){
        return REGISTERED_ITEM_VARIANTS.stream().filter(dyeableItemVariant -> dyeableItemVariant.parentVariantIdentifier == null).collect(Collectors.toSet());
    }

    /**
     * Safe way of making sure all variants are added to the main Set that contains all the Registered Block Item Variants
     * @return {@link #REGISTERED_BLOCK_ITEM_VARIANTS} safely
     */
    public static Set<DyeableItemVariant> getAllBlockItemVariants(){
        return REGISTERED_BLOCK_ITEM_VARIANTS;
    }

    public static Set<DyeableItemVariant> AllBaseBlockItemVariants(){
        return REGISTERED_BLOCK_ITEM_VARIANTS.stream().filter(dyeableItemVariant -> dyeableItemVariant.parentVariantIdentifier != null).collect(Collectors.toSet());
    }

    /**
     * Returns a String from the given {@link DyeColorant} and the {@link #variantIdentifier}
     * @param dyeColorant Desired Color
     */
    @ApiStatus.Internal
    public String getColoredEntryPath(DyeColorant dyeColorant) {
        return dyeColorant.getName() + "_" + this.variantIdentifier.getPath();
    }

    /**
     * Attempts to get the color from a possible Variant.
     *
     * @param convertible Possible Item of the given Variant
     * @return The DyeColorant of the entry or null if the given it isn't a given Variant
     */
    @Nullable
    @ApiStatus.Internal
    public DyeColorant getColorFromEntry(ItemConvertible convertible){
        Identifier identifier = BetterItemOps.getIdFromConvertible(convertible);

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

        return DyeColorant.byName(stringBuilder.toString(), DyeColorantRegistry.NULL_VALUE_NEW);
    }

    public BlockColorManipulators.AlterItemColor getAlterItemColorMethod(){
        return this.itemColorChangeMethod;
    }

    //---------------------------------------------------------------------------------------------------

    @ApiStatus.Internal
    public boolean createItem(){
        return itemMaker != null;
    }

    @ApiStatus.Internal
    public final void addToTags(ItemConvertible entry, boolean readOnly){
        this.addToItemTags(entry.asItem(), readOnly);
    }

    private boolean initilizedDefaultItemTag = false;

    @ApiStatus.Internal
    protected final void addToItemTags(Item item, boolean readOnly) {
        if(item == Blocks.AIR.asItem()){
            return;
        }

        TagInjector.inject(Registry.ITEM, GelatinTags.Items.ALL_COLORED_VARIANTS.id(), item);
        TagInjector.inject(Registry.ITEM, getPrimaryItemTag().id(), item);

        if(!readOnly) {
            for (TagKey<Item> tagKey : allItemTags.subList(1, allItemTags.size())) {
                TagInjector.inject(Registry.ITEM, tagKey.id(), item);
            }
        }

        if(!defaultEntryIdentifier.getPath().contains("white") && !initilizedDefaultItemTag){ //item != this.getDefaultItem() &&
            initilizedDefaultItemTag = true;

            this.addToItemTags(this.getDefaultItem(), true);
        }
    }

    @ApiStatus.Internal
    public Item makeItem(DyeColorant dyeColorant, @Nullable ItemConvertible parentEntry, Item.Settings settings) {
        return this.itemMaker.createItemFromDyeColor(dyeColorant, parentEntry, BetterItemOps.copyFrom(settings));
    }
    
}
