package io.wispforest.jello.api.dye.registry.variants.item;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.ColorManipulators;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableVariant;
import io.wispforest.jello.api.dye.registry.variants.DyeableVariantManager;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import io.wispforest.jello.api.item.JelloItemSettings;
import io.wispforest.jello.data.tags.JelloTags;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

/**
 * A {@link DyeableItemVariant} is a way to add your own
 * Dyed Item Variants to Jello's System so that
 * any {@link DyeColorant} created gets made with your Variant.
 */
public class DyeableItemVariant extends DyeableVariant<DyeableItemVariant> {

    public @Nullable ItemMaker itemMaker;
    public Item.Settings defaultItemSettings;

    public final List<TagKey<Item>> allItemTags = new ArrayList<>();

    public ColorManipulators.AlterItemColor itemColorChangeMethod = ColorManipulators.AlterItemColor.DEFAULT_ITEM;

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

        allItemTags.add(TagKey.of(Registry.ITEM_KEY, Jello.id(variantIdentifier.getPath())));
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

        /**
         * Method must be called when the Variant is finished being edited
         * Will add your variant to the {@link #REGISTERED_ITEM_VARIANTS} and
         * retroactively add this {@link DyeableItemVariant} and {@link DyeableVariantManager#updateExistingDataForItem}
         */
        public DyeableItemVariant register() {
            if (!DyeableItemVariant.REGISTERED_ITEM_VARIANTS.contains(itemVariant)) {
                DyeableVariantManager.updateExistingDataForItem(itemVariant);
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

        if(!dyeColorant.isIn(JelloTags.DyeColor.VANILLA_DYES)) {
            if (Objects.equals(nameSpace, "minecraft")) {
                nameSpace = dyeColorant.getId().getNamespace();
            }
        }

        return Registry.ITEM.get(new Identifier(nameSpace, getColoredEntryPath(dyeColorant)));
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
        return getVariantFromItem(JelloItemSettings.getIdFromConvertible(convertible));
    }

    /**
     * Attempts to get a {@link DyeableItemVariant} from a given {@link Identifier}
     * @param identifier possible identifier
     * @return {@link DyeableItemVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    private static DyeableItemVariant getVariantFromItem(Identifier identifier){
        for(DyeableItemVariant variant : getAllItemVariants()){
            if(variant.isSuchAVariant(identifier)){
                return variant;
            }
        }

        return null;
    }

    /**
     * Safe way of making sure all variants are added to the main Set that contains all the Registered Item Variants
     * @return {@link #ALL_ITEM_VARIANTS} safely
     */
    public static Set<DyeableItemVariant> getAllItemVariants(){
        //TODO: Is such really needed?

        if(ALL_ITEM_VARIANTS.isEmpty() || ALL_ITEM_VARIANTS.size() < DyeableVariantManager.getVariantMap().get(DyeColorantRegistry.WHITE).dyedItems().size()){
            for(DyeableItemVariant dyeableItemVariant : REGISTERED_ITEM_VARIANTS){
                addToAllItemVariantsRecursive(dyeableItemVariant);
            }
        }

        return ALL_ITEM_VARIANTS;
    }

    private static void addToAllItemVariantsRecursive(DyeableItemVariant dyeableItemVariant){
        ALL_ITEM_VARIANTS.add(dyeableItemVariant);
        if(dyeableItemVariant.childVariant.get() != null){
            addToAllItemVariantsRecursive(dyeableItemVariant.childVariant.get());
        }
    }

    /**
     * Safe way of making sure all variants are added to the main Set that contains all the Registered Block Item Variants
     * @return {@link #ALL_BLOCK_ITEM_VARIANTS} safely
     */
    public static Set<DyeableItemVariant> getAllBlockItemVariants(){
        //TODO: Is such really needed?

        if(ALL_BLOCK_ITEM_VARIANTS.isEmpty() ||
                ALL_BLOCK_ITEM_VARIANTS.size() < DyeableVariantManager.getVariantMap().get(DyeColorantRegistry.WHITE).dyedBlocks().entrySet().stream().filter(entry -> entry.getKey().createBlockItem()).count()){
            for(DyeableItemVariant dyeableItemVariant : REGISTERED_BLOCK_ITEM_VARIANTS){
                addToAllBlockItemVariantsRecursive(dyeableItemVariant);
            }
        }

        return ALL_BLOCK_ITEM_VARIANTS;
    }

    private static void addToAllBlockItemVariantsRecursive(DyeableItemVariant dyeableItemVariant){
        ALL_ITEM_VARIANTS.add(dyeableItemVariant);
        if(dyeableItemVariant.childVariant.get() != null){
            addToAllItemVariantsRecursive(dyeableItemVariant.childVariant.get());
        }
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

    public ColorManipulators.AlterItemColor getAlterItemColorMethod(){
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

        TagInjector.inject(Registry.ITEM, JelloTags.Items.ALL_COLORED_VARIANTS.id(), item);
        TagInjector.inject(Registry.ITEM, getPrimaryItemTag().id(), item);

        if(!readOnly) {
            for (TagKey<Item> tagKey : allItemTags.subList(1, allItemTags.size())) {
                TagInjector.inject(Registry.ITEM, tagKey.id(), item);
            }
        }

        if(defaultEntryIdentifier.getPath().contains("white") && item != this.getDefaultItem() && !initilizedDefaultItemTag){
            this.addToItemTags(this.getDefaultItem(), true);
            initilizedDefaultItemTag = true;
        }
    }

    @ApiStatus.Internal
    public Item makeItem(DyeColorant dyeColorant, @Nullable ItemConvertible parentEntry, Item.Settings settings) {
        return this.itemMaker.createItemFromDyeColor(dyeColorant, parentEntry, JelloItemSettings.copyFrom(settings));
    }
    
}
