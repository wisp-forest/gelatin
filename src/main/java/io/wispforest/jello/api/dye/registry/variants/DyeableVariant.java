package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.item.DyeableItemVariant;
import io.wispforest.jello.api.item.JelloItemSettings;
import io.wispforest.jello.data.tags.JelloTags;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public abstract class DyeableVariant<T extends DyeableVariant<T>> {

    public static final Set<DyeableItemVariant> ALL_ITEM_VARIANTS = new LinkedHashSet<>();
    public static final Set<DyeableItemVariant> REGISTERED_ITEM_VARIANTS = new LinkedHashSet<>();

    public static final Set<DyeableBlockVariant> ALL_BLOCK_VARIANTS = new LinkedHashSet<>();
    public static final Set<DyeableBlockVariant> REGISTERED_BLOCK_VARIANTS = new LinkedHashSet<>();

    public static final Set<DyeableItemVariant> ALL_BLOCK_ITEM_VARIANTS = new LinkedHashSet<>();
    public static final Set<DyeableItemVariant> REGISTERED_BLOCK_ITEM_VARIANTS = new LinkedHashSet<>();

    /**
     * None: The type telling the builder that is a single block and has no other variants that depend on this block to build
     * Chain: The type telling the builder that this DyeableVariant needs a Entry to be created first
     */
    public enum RecursiveType {
        NONE(),
        CHAINED()
    }

    public final Identifier variantIdentifier;
    public final int wordCount;

    public Identifier defaultEntryIdentifier;

    protected boolean vanillaColorsOnly = false;

    public RecursiveType recursiveType = RecursiveType.NONE;
    public Supplier<T> childVariant = () -> null;

    public DyeableVariant(Identifier variantIdentifier, @Nullable Supplier<T> possibleChildVariant){
        this.variantIdentifier = variantIdentifier;
        this.wordCount = variantIdentifier.getPath().split("_").length;

        if (possibleChildVariant != null) {
            this.recursiveType = DyeableBlockVariant.RecursiveType.CHAINED;
            this.childVariant = possibleChildVariant;
        }
    }

    public final boolean vanillaDyesOnly(){
        return this.vanillaColorsOnly;
    }

    //-------------------------------------------------------------------------------------

    /**
     * Checks if the given {@link ItemConvertible}, which can be a block or Item, is a Variant of the Given Variant.
     *
     * @param convertible possible variant
     * @return True if the given Entry is a Variant
     */
    public boolean isSuchAVariant(ItemConvertible convertible) {
        return this.isSuchAVariant(JelloItemSettings.getIdFromConvertible(convertible));
    }

    @ApiStatus.Internal
    public boolean isSuchAVariant(Identifier identifier) {
        if(Objects.equals(identifier.getPath(), defaultEntryIdentifier.getPath()))
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
     * Attempts to get a {@link DyeableBlockVariant} from a given {@link ItemConvertible}
     * @param convertible possible Block or Item of a {@link DyeableBlockVariant}
     * @return {@link DyeableVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    public static <T extends DyeableVariant> T getVariantFromEntry(ItemConvertible convertible){
        return getVariantFromEntry(JelloItemSettings.getIdFromConvertible(convertible));
    }

    /**
     * Attempts to get a {@link DyeableBlockVariant} from a given {@link Identifier}
     * @param identifier possible identifier
     * @return {@link DyeableItemVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    private static <T extends DyeableVariant> T getVariantFromEntry(Identifier identifier){
        for(DyeableVariant variant : getAllVariants()){
            if(variant.isSuchAVariant(identifier)){
                return (T) variant;
            }
        }

        return null;
    }

    /**
     * Gets an Entry based off the given {@link DyeColorant} and the {@link #variantIdentifier} of the Variant used
     * @param dyeColorant Desired Color or default entry if it is {@link DyeColorantRegistry#NULL_VALUE_NEW}
     */
    public abstract ItemConvertible getColoredEntry(DyeColorant dyeColorant);

    /**
     * If the given Variant is only trying to use Jello's API rather than creating entry's
     */
    public abstract boolean alwaysReadOnly();

    /**
     * Attempts to check if a Block is Dyeable and if it is will attempt to use the Variant to get the Colored Block passed to it
     *
     * @param entry Possibly Colorable Block
     * @param dyeColorant Color being applied to the Block
     * @return A block if the Variant exists and meets certain parameters within the Variant else it returns null
     */
    @Nullable
    public static ItemConvertible attemptToGetColoredEntry(ItemConvertible entry, DyeColorant dyeColorant){
        DyeableVariant variant;

        if(entry instanceof Block || entry instanceof BlockItem) {
            variant = DyeableBlockVariant.getVariantFromBlock(entry);
        } else {
            variant = DyeableItemVariant.getVariantFromItem(entry);
        }

        if (variant != null) {
            if (variant.vanillaDyesOnly() && !dyeColorant.isIn(JelloTags.DyeColor.VANILLA_DYES)) {
                return null;
            }

            return variant.getColoredEntry(dyeColorant);
        } else {
            return null;
        }
    }

    /**
     * Safe way of making sure all variants are added to the main Set that contains all the Registered Block Variants
     * @return {@link #ALL_BLOCK_VARIANTS} safely
     */
    public static <T extends DyeableVariant> Set<T> getAllVariants(){
        Set<T> allVariants = new HashSet<>();

        allVariants.addAll((Collection<? extends T>) DyeableBlockVariant.getAllBlockVariants());
        allVariants.addAll((Collection<? extends T>) DyeableItemVariant.getAllItemVariants());

        return allVariants;
    }
}
