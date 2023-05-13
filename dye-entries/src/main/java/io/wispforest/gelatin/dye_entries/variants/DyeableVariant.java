package io.wispforest.gelatin.dye_entries.variants;

import io.wispforest.gelatin.common.util.ItemFunctions;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_entries.variants.item.DyeableItemVariant;
import io.wispforest.gelatin.dye_registry.data.GelatinTags;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class DyeableVariant<T extends DyeableVariant<T, V>, V extends ItemConvertible> {

    public final Identifier variantIdentifier;
    public final int wordCount;

    public final Registry<V> registry;

    public Identifier defaultEntryIdentifier;

    protected boolean vanillaColorsOnly = false;

    public List<Supplier<T>> childVariant = new ArrayList<>();

    @Nullable
    public Identifier parentVariantIdentifier = null;

    public List<TagKey<V>> allTags = new ArrayList<>();

    public DyeableVariant(Identifier variantIdentifier, Registry<V> registry){
        this.variantIdentifier = variantIdentifier;
        this.wordCount = variantIdentifier.getPath().split("_").length;

        this.registry = registry;
    }

    @Deprecated
    public DyeableVariant(Identifier variantIdentifier, @Nullable Supplier<T> possibleChildVariant, Registry<V> registry){
        this(variantIdentifier, registry);

        if (possibleChildVariant != null) this.childVariant.add(possibleChildVariant);
    }

    public final boolean vanillaDyesOnly(){
        return this.vanillaColorsOnly;
    }

    public final boolean customDefaultBlock() {
        return !defaultEntryIdentifier.getPath().contains("white");
    }

    //-------------------------------------------------------

    public T configureVariant(Consumer<T> configuration){
        configuration.accept((T) this);

        return (T) this;
    }

    /**
     * Disables the creation of Modded Dyed Variants and only allows for Coloring this item with Vanilla Colors
     */
    public final T restrictToVanillaDyes(boolean value){
        this.vanillaColorsOnly = value;

        return (T) this;
    }

    /**
     * Used to link Variants together if they depend on this one to be build first
     */
    public T linkChildEntry(Supplier<T> possibleChildVariant){
        this.childVariant.add(possibleChildVariant);

        return (T) this;
    }

    /**
     * Manually change the {@link #defaultEntryIdentifier} Identifier by combining the Item's path and the variant's MODID
     *
     * @param path The Item's default path
     */
    public T setDefaultEntry(String path) {
        return this.setDefaultEntry(new Identifier(this.variantIdentifier.getNamespace(), path));
    }

    /**
     * Manually change the {@link #defaultEntryIdentifier} Identifier
     *
     * @param identifier The identifier of the item
     */
    public T setDefaultEntry(Identifier identifier) {
        this.defaultEntryIdentifier = identifier;

        return (T) this;
    }

    @SafeVarargs
    public final T addTags(TagKey<V>... tags) {
        return this.addTags(Arrays.asList(tags));
    }

    public T addTags(Collection<TagKey<V>> tags) {
        this.allTags.addAll(tags);

        return (T) this;
    }

    public T setParentId(Identifier parentVariantId){
        this.parentVariantIdentifier = parentVariantId;

        return (T) this;
    }

    //-------------------------------------------------------------------------------------

    /**
     * Checks if the given {@link ItemConvertible}, which can be a block or Item, is a Variant of the Given Variant.
     *
     * @param convertible possible variant
     * @return True if the given Entry is a Variant
     */
    public boolean isSuchAVariant(ItemConvertible convertible, boolean tagCheck) {
        return this.isSuchAVariant(ItemFunctions.getIdFromConvertible(convertible), tagCheck);
    }

    @ApiStatus.Internal
    public boolean isSuchAVariant(Identifier identifier, boolean tagCheck) {
        if(Objects.equals(identifier.getPath(), defaultEntryIdentifier.getPath())) return true;

        String[] pathParts = identifier.getPath().split("_");

        if (pathParts.length <= wordCount) return false;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = pathParts.length - wordCount; i < pathParts.length; i++) {
            stringBuilder.append(pathParts[i]);

            if (i < pathParts.length - 1) stringBuilder.append("_");
        }

        if(stringBuilder.toString().equals(this.variantIdentifier.getPath())){
            if(!tagCheck) return true;

            V entry = getEntryFromIdentifier(identifier);

            if (entry instanceof Item item) return item.getRegistryEntry().isIn((TagKey<Item>) getPrimaryTag());
            if (entry instanceof Block block) return block.getRegistryEntry().isIn((TagKey<Block>) getPrimaryTag());
        }

        return false;
    }

    @Nullable
    protected V getEntryFromIdentifier(Identifier identifier){
        return this.registry.get(identifier);
    }

    /**
     * The Common tag based off the {@link #getPrimaryTag()} that is made from the {@link #variantIdentifier}
     *
     * @return A Block Tag within fabric's common namespace from the variant used
     */
    public final TagKey<V> getCommonTag() {
        return TagKey.of(this.registry.getKey(), new Identifier("c", getPrimaryTag().id().getPath()));
    }

    public TagKey<V> getPrimaryTag(){
        return allTags.get(0);
    }

    private boolean initDefaultBlockTag = false;

    @ApiStatus.Internal
    public void addToTags(V convertible, boolean readOnly) {
        Registry<V> registry = ((Registry<Registry<V>>) Registry.REGISTRIES).get((RegistryKey<Registry<V>>) getPrimaryTag().registry()); //this.registry

        TagInjector.inject(registry, getPrimaryTag().id(), convertible);

        if(!readOnly) {
            for (TagKey<V> tagKey : allTags.subList(1, allTags.size())) {
                TagInjector.inject(registry, tagKey.id(), convertible);
            }
        }

        if(!defaultEntryIdentifier.getPath().contains("white") && !initDefaultBlockTag) { //&& block != this.getDefaultBlock()
            initDefaultBlockTag = true;

            this.addToTags(this.getDefaultEntry(), true);
        }
    }

    /**
     * @return An Entry based on the given {@link #defaultEntryIdentifier}.
     */
    @Nullable
    public V getDefaultEntry(){
        return this.registry.get(this.defaultEntryIdentifier);
    }

    /**
     * Gets an Entry based off the given {@link DyeColorant} and the {@link #variantIdentifier} of the Variant used
     * @param dyeColorant Desired Color or default entry if it is {@link DyeColorantRegistry#NULL_VALUE_NEW}
     */
    @Nullable
    public V getColoredEntry(DyeColorant dyeColorant) {
        if(dyeColorant == DyeColorantRegistry.NULL_VALUE_NEW) return this.getDefaultEntry();

        String nameSpace = this.variantIdentifier.getNamespace();

        if(!dyeColorant.isIn(GelatinTags.DyeColor.VANILLA_DYES) && Objects.equals(nameSpace, "minecraft")) {
            nameSpace = dyeColorant.getId().getNamespace();
        }

        return getEntryFromIdentifier(new Identifier(nameSpace, getColoredEntryPath(dyeColorant)));
    }

    /**
     * Returns a String from the given {@link DyeColorant} and the {@link #variantIdentifier}
     * @param dyeColorant Desired Color
     */
    public String getColoredEntryPath(DyeColorant dyeColorant) {
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
    public DyeColorant getColorFromEntry(V convertible){
        Identifier identifier = ItemFunctions.getIdFromConvertible(convertible);

        if(!this.isSuchAVariant(identifier, true)) return null;

        String[] pathParts = identifier.getPath().split("_");

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pathParts.length - wordCount; i++) {
            stringBuilder.append(pathParts[i]);

            if (i < pathParts.length - wordCount - 1) stringBuilder.append("_");
        }

        return DyeColorant.byName(stringBuilder.toString(), DyeColorantRegistry.NULL_VALUE_NEW);
    }

    /**
     * If the given Variant is only trying to use Jello's API rather than creating entry's
     */
    public abstract boolean alwaysReadOnly();

    @Override
    public String toString() {
        return this.variantIdentifier.toString();
    }

    //----------------------------------------------------------------------------------------

    /**
     * Attempts to get a {@link DyeableVariant} from a given {@link ItemConvertible}
     * @param convertible possible Block or Item of a {@link DyeableVariant}
     * @return {@link DyeableVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    public static <T extends DyeableVariant> T getVariantFromEntry(ItemConvertible convertible){
        return getVariantFromEntry(ItemFunctions.getIdFromConvertible(convertible));
    }

    /**
     * Attempts to get a {@link DyeableVariant} from a given {@link Identifier}
     * @param identifier possible identifier
     * @return {@link DyeableVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    private static <T extends DyeableVariant> T getVariantFromEntry(Identifier identifier){
        for(DyeableVariant variant : DyeableVariantRegistry.getAllVariants()){
            if(variant.isSuchAVariant(identifier, true)) return (T) variant;
        }

        return null;
    }

    /**
     * Attempts to check if a Block is Dyeable and if it is attempt to use the Variant to get the Colored Block passed to it
     *
     * @param entry Possibly Colorable Block
     * @param dyeColorant Color being applied to the Block
     * @return A block if the Variant exists and meets certain parameters within the Variant else it returns null
     */
    @Nullable
    public static ItemConvertible attemptToGetColoredEntry(ItemConvertible entry, DyeColorant dyeColorant){
        DyeableVariant variant = (entry instanceof Block || entry instanceof BlockItem)
                ? DyeableBlockVariant.getVariantFromBlock(entry)
                : DyeableItemVariant.getVariantFromItem(entry);

        if (variant == null || variant.vanillaDyesOnly() && !dyeColorant.isIn(GelatinTags.DyeColor.VANILLA_DYES)) return null;

        return variant.getColoredEntry(dyeColorant);
    }

}
