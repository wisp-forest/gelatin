package io.wispforest.gelatin.dye_entries.variants;

import com.google.common.collect.ImmutableSet;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_entries.variants.item.DyeableItemVariant;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DyeableVariantRegistry {

    public static final DyeableVariantRegistry INSTANCE = new DyeableVariantRegistry();

    /**
     * Sets that contain all the given variants that were registered
     */
    private final Set<DyeableItemVariant> REGISTERED_ITEM_VARIANTS = new LinkedHashSet<>();
    private final Set<DyeableBlockVariant> REGISTERED_BLOCK_VARIANTS = new LinkedHashSet<>();
    private final Set<DyeableItemVariant> REGISTERED_BLOCK_ITEM_VARIANTS = new LinkedHashSet<>();

    //-------------------

    public void registerItemVariant(DyeableItemVariant variant){
        if (!REGISTERED_ITEM_VARIANTS.contains(variant) && variant.parentVariantIdentifier == null) {
            DyeableVariantManager.updateExistingDataForItem(variant);
        }

        //ColorBlockRegistry.registerBlockTypeWithRecursion(this);
        REGISTERED_ITEM_VARIANTS.add(variant);
    }

    public void registerBlockVariant(DyeableBlockVariant variant){
        if (!REGISTERED_BLOCK_VARIANTS.contains(variant) && variant.parentVariantIdentifier == null) {
            DyeableVariantManager.updateExistingDataForBlock(variant);
        }

        //ColorBlockRegistry.registerBlockTypeWithRecursion(this);
        REGISTERED_BLOCK_VARIANTS.add(variant);

        if(variant.createBlockItem()) REGISTERED_BLOCK_ITEM_VARIANTS.add(variant.blockItemVariant);
    }

    //-------------------

    public static Set<DyeableBlockVariant> getAllBlockVariants(){
        return INSTANCE.REGISTERED_BLOCK_VARIANTS;
    }

    public static Set<DyeableBlockVariant> getParentBlockVariants(){
        return INSTANCE.REGISTERED_BLOCK_VARIANTS.stream().filter(dyeableItemVariant -> dyeableItemVariant.parentVariantIdentifier == null).collect(Collectors.toSet());
    }

    public static Set<DyeableItemVariant> getAllItemVariants(){
        return INSTANCE.REGISTERED_ITEM_VARIANTS;
    }

    public static Set<DyeableItemVariant> getParentItemVariants(){
        return INSTANCE.REGISTERED_ITEM_VARIANTS.stream().filter(dyeableItemVariant -> dyeableItemVariant.parentVariantIdentifier == null).collect(Collectors.toSet());
    }

    public static Set<DyeableItemVariant> getAllBlockItemVariants(){
        return INSTANCE.REGISTERED_BLOCK_ITEM_VARIANTS;
    }

    public static Set<DyeableItemVariant> getParentBlockItemVariants(){
        return INSTANCE.REGISTERED_BLOCK_ITEM_VARIANTS.stream().filter(dyeableItemVariant -> dyeableItemVariant.parentVariantIdentifier != null).collect(Collectors.toSet());
    }

    /**
     * Safe way of making sure all variants are added to the main Set that contains all the Registered Block Variants
     * @return {@link DyeableVariantRegistry#REGISTERED_BLOCK_VARIANTS} safely
     */
    public static <T extends DyeableVariant> Set<T> getAllVariants(){
        Set<T> allVariants = new HashSet<>();

        allVariants.addAll((Collection<? extends T>) getAllBlockVariants());
        allVariants.addAll((Collection<? extends T>) getAllItemVariants());

        return allVariants;
    }
}
