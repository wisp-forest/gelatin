package io.wispforest.gelatin.dye_entries.variants.block;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.common.util.ItemFunctions;
import io.wispforest.gelatin.common.events.LootTableInjectionEvent;
import io.wispforest.gelatin.common.util.VersatileLogger;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantRegistry;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_entries.BlockColorManipulators;
import io.wispforest.gelatin.dye_entries.data.GelatinLootTables;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariant;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantManager;
import io.wispforest.gelatin.dye_entries.variants.item.DyeableItemVariant;
import io.wispforest.gelatin.dye_entries.variants.item.ItemMaker;
import io.wispforest.gelatin.dye_entries.variants.impl.VanillaBlockVariants;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootTable;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link DyeableBlockVariant} is a way to add your own
 * Dyed Block Variants, like Minecraft's Wool and Concrete,
 * to Jello's System so that any {@link DyeColorant}
 * created gets made with your Variant.
 */
public class DyeableBlockVariant extends DyeableVariant<DyeableBlockVariant, Block> {

    private @Nullable final BlockMaker blockMaker;
    public final DyeableItemVariant blockItemVariant;

    protected Function<Block, LootTable> lootTableBuilder = (itemConvertible) -> GelatinLootTables.drops(itemConvertible).build();

    protected BlockColorManipulators.AlterBlockColor colorChangeMethod = BlockColorManipulators.AlterBlockColor.DEFAULT;

    /**
     * @param variantIdentifier The {@link Identifier} based off your Modid and the block path for your variant
     * @param itemSettings The {@link Item.Settings} used when creating the blockItem
     * @param blockMaker A generalized way of creating your Block Variant (Look at {@link VanillaBlockVariants})
     */
    public DyeableBlockVariant(Identifier variantIdentifier, Item.Settings itemSettings, @Nullable BlockMaker blockMaker) {
        super(variantIdentifier, Registry.BLOCK);

        this.blockItemVariant = new DyeableItemVariant(variantIdentifier, itemSettings, ItemMaker.BLOCK_DEFAULT);
        this.blockItemVariant.itemColorChangeMethod = BlockColorManipulators.AlterItemColor.DEFAULT_BLOCK;

        this.blockMaker = blockMaker;

        this.defaultEntryIdentifier = new Identifier(variantIdentifier.getNamespace(), "white_" + variantIdentifier.getPath());

        allTags.add(TagKey.of(Registry.BLOCK_KEY, GelatinConstants.id(variantIdentifier.getPath())));
    }

    public static DyeableBlockVariant readOnly(Identifier variantIdentifier){
        return new DyeableBlockVariant(variantIdentifier, new Item.Settings(), null);
    }

    //---------------------------------------------------------------------------------------------------

    /**
     * This method sets the {@link BlockColorManipulators.AlterBlockColor} for this variant, which is used
     * internally for when a player is going to Dye a Colorable block of this Variant using a DyeItem or such.
     * <br><br>
     * Allows for blocks that are two parts like bed or BlockEntity's that need special things like NBT data to be copyied to change states properly
     */
    public DyeableBlockVariant setBlockStateChangeMethod(BlockColorManipulators.AlterBlockColor method){
        this.colorChangeMethod = method;

        return this;
    }

    /**
     * Change the default Function for automatically generating the {@link LootTable} for this Block Variant
     */
    public DyeableBlockVariant setLootTable(Function<Block, LootTable> lootTableBuilder){
        this.lootTableBuilder = lootTableBuilder;

        return this;
    }

    public DyeableBlockVariant configureBlockItemVariant(Consumer<DyeableItemVariant> configure){
        configure.accept(this.blockItemVariant);

        return this;
    }

    /**
     * Disables creation of BlockItem for the given variant
     */
    public final DyeableBlockVariant noBlockItem(){
        this.blockItemVariant.itemMaker = null;
        this.blockItemVariant.defaultItemSettings = new Item.Settings();

        return this;
    }

    /**
     * Method must be called when the Variant is finished being edited
     * Will add your variant to the {@link DyeableVariantRegistry#REGISTERED_BLOCK_VARIANTS} and
     * retroactively add this {@link DyeableBlockVariant} and {@link DyeableVariantManager#updateExistingDataForItem}
     */
    public final DyeableBlockVariant register() {
        DyeableVariantRegistry.INSTANCE.registerBlockVariant(this);

        return this;
    }

    //---------------------------------------------------------------------------------------------------

    public boolean alwaysReadOnly(){
        return this.blockMaker == null;
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
        return blockItemVariant.getDefaultEntry();
    }

    //---------------------------------------------------------------------------------------------------

    /**
     * Attempts to get a {@link DyeableBlockVariant} from a given {@link ItemConvertible}
     * @param convertible possible Block or Item of a {@link DyeableBlockVariant}
     * @return {@link DyeableBlockVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    public static DyeableBlockVariant getVariantFromBlock(ItemConvertible convertible){
        return getVariantFromBlock(ItemFunctions.getIdFromConvertible(convertible));
    }

    /**
     * Attempts to get a {@link DyeableBlockVariant} from a given {@link Identifier}
     * @param identifier possible identifier
     * @return {@link DyeableBlockVariant} or null if the given Entry doesn't have one
     */
    @Nullable
    private static DyeableBlockVariant getVariantFromBlock(Identifier identifier){
        for(DyeableBlockVariant variant : DyeableVariantRegistry.getAllBlockVariants()){
            if(variant.isSuchAVariant(identifier, true)) return variant;
        }

        return null;
    }

    public BlockColorManipulators.AlterBlockColor getAlterColorMethod(){
        return this.colorChangeMethod;
    }

    //---------------------------------------------------------------------------------------------------

    @ApiStatus.Internal
    public boolean createBlockItem(){
        return blockItemVariant.itemMaker != null;
    }

    @ApiStatus.Internal
    public void addToTags(Block block, boolean readOnly){
        super.addToTags(block, readOnly);

        if(createBlockItem()) this.blockItemVariant.addToTags(block.asItem(), readOnly);
    }

    @ApiStatus.Internal
    public void generateAllLootTables(LootTableInjectionEvent.LootTableMapHelper helper){
        if(alwaysReadOnly() || !createBlockItem()) return;

        Set<DyeColorant> dyeColorants = new HashSet<>(vanillaColorsOnly
                ? DyeColorantRegistry.Constants.VANILLA_DYES
                : DyeColorantRegistry.DYE_COLOR.stream().toList()
        );

        VersatileLogger logger = new VersatileLogger("DyeableBlockVariantLootTables", () -> GelatinConstants.DEBUG_ENV_VAR | GelatinConstants.DEBUG_ENV);

        for(DyeColorant dyeColorant : dyeColorants){
            Block block = this.getColoredEntry(dyeColorant);

            Identifier blockId = Registry.BLOCK.getId(block);

            if(Objects.equals(blockId.getNamespace(), "minecraft") || (Objects.equals(this.defaultEntryIdentifier.getPath(), blockId.getPath()) && this.customDefaultBlock()))
                continue;

            if(!helper.addLootTable(block.getLootTableId(), this.lootTableBuilder.apply(block))){
                logger.failMessage("Seems that a lootTable for a block [lootTableId: {}, colorId: {}, blockId: {}, variantId: {}] already exists, will not be added then", block.getLootTableId(), dyeColorant, block, this);
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
}
