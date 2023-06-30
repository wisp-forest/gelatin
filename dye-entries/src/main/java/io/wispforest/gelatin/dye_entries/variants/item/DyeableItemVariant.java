package io.wispforest.gelatin.dye_entries.variants.item;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.common.util.ItemFunctions;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantRegistry;
import io.wispforest.gelatin.dye_registry.DyeColorant;
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

/**
 * A {@link DyeableItemVariant} is a way to add your own
 * Dyed Item Variants to Jello's System so that
 * any {@link DyeColorant} created gets made with your Variant.
 */
public class DyeableItemVariant extends DyeableVariant<DyeableItemVariant, Item> {

    public @Nullable ItemMaker itemMaker;
    public Item.Settings defaultItemSettings;

    public BlockColorManipulators.AlterItemColor itemColorChangeMethod = BlockColorManipulators.AlterItemColor.DEFAULT_ITEM;

    /**
     * @param variantIdentifier The {@link Identifier} based off your Modid and the block path for your variant
     * @param itemSettings The {@link Item.Settings} used when creating the blockItem
     * @param itemMaker A generalized way of creating your Item Variant
     */
    public DyeableItemVariant(Identifier variantIdentifier, Item.Settings itemSettings, @Nullable ItemMaker itemMaker) {
        super(variantIdentifier, Registry.ITEM);

        this.itemMaker = itemMaker;
        this.defaultItemSettings = itemSettings;

        this.defaultEntryIdentifier = new Identifier(variantIdentifier.getNamespace(), "white_" + variantIdentifier.getPath());

        allTags.add(TagKey.of(Registry.ITEM_KEY, GelatinConstants.id(variantIdentifier.getPath())));
    }

    public static DyeableItemVariant readOnly(Identifier variantIdentifier){
        return new DyeableItemVariant(variantIdentifier, new Item.Settings(), null);
    }

    /**
     * Method must be called when the Variant is finished being edited
     * Will add your variant to the {@link DyeableVariantRegistry#REGISTERED_ITEM_VARIANTS} and
     * retroactively add this {@link DyeableItemVariant} and {@link DyeableVariantManager#updateExistingDataForItem}
     */
    public DyeableItemVariant register() {
        DyeableVariantRegistry.INSTANCE.registerItemVariant(this);

        return this;
    }

    //---------------------------------------------------

    public boolean alwaysReadOnly(){
        return this.itemMaker == null;
    }

    //---------------------------------------------------------------------------------------------------

    /**
     * Attempts to get a {@link DyeableBlockVariant} from a given {@link ItemConvertible}
     * @param convertible possible Block or Item of a {@link DyeableBlockVariant}
     * @return {@link DyeableVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    public static DyeableItemVariant getVariantFromItem(ItemConvertible convertible){
        return getVariantFromItem(ItemFunctions.getIdFromConvertible(convertible));
    }

    /**
     * Attempts to get a {@link DyeableItemVariant} from a given {@link Identifier}
     * @param identifier possible identifier
     * @return {@link DyeableItemVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    public static DyeableItemVariant getVariantFromItem(Identifier identifier){
        for(DyeableItemVariant variant : DyeableVariantRegistry.getAllItemVariants()){
            if(variant.isSuchAVariant(identifier, true)) return variant;
        }

        return null;
    }

    public BlockColorManipulators.AlterItemColor getAlterItemColorMethod(){
        return this.itemColorChangeMethod;
    }

    //---------------------------------------------------------------------------------------------------

    @ApiStatus.Internal
    public boolean createItem(){
        return itemMaker != null;
    }

    @Override
    public void addToTags(Item item, boolean readOnly) {
        if(item == Blocks.AIR.asItem()) return;

        TagInjector.inject(Registry.ITEM, GelatinTags.Items.ALL_COLORED_VARIANTS.id(), item);
        TagInjector.inject(Registry.ITEM, getPrimaryTag().id(), item);

        super.addToTags(item, readOnly);
    }

    @ApiStatus.Internal
    public Item makeItem(DyeColorant dyeColorant, @Nullable ItemConvertible parentEntry, Item.Settings settings) {
        return this.itemMaker.createItemFromDyeColor(dyeColorant, parentEntry, ItemFunctions.copyFrom(settings));
    }
    
}
