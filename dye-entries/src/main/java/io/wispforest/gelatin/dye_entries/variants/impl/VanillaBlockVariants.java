package io.wispforest.gelatin.dye_entries.variants.impl;

import io.wispforest.gelatin.common.util.ItemFunctions;
import io.wispforest.gelatin.dye_entries.block.*;
import io.wispforest.gelatin.dye_entries.data.GelatinLootTables;
import io.wispforest.gelatin.dye_entries.misc.DyeEntriesItemGroups;
import io.wispforest.gelatin.dye_entries.mixins.accessors.BlockEntityTypeAccessor;
import io.wispforest.gelatin.dye_entries.mixins.accessors.ShulkerBoxBlockEntityAccessor;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An implementation of all vanilla Dyeable blocks used when a {@link DyeColorant} is registered for Variant creation
 */
public class VanillaBlockVariants {

    private static final Item.Settings itemSettings = DyeEntriesItemGroups.getItemSettings.apply(1);

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant CONCRETE_POWDER = new DyeableBlockVariant(new Identifier("concrete_powder"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredConcretePowderBlock(parentBlock, AbstractBlock.Settings.of(Material.AGGREGATE, MapColor.CLEAR).strength(0.5F).sounds(BlockSoundGroup.SAND), dyeColorant);
    }).addTags(BlockTags.PICKAXE_MINEABLE, BlockTags.NEEDS_STONE_TOOL)
            .setParentId(new Identifier("concrete"))
            .register();

    public static final DyeableBlockVariant CONCRETE = new DyeableBlockVariant(new Identifier("concrete"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.CLEAR).requiresTool().strength(1.8F), dyeColorant);
    }).addTags(BlockTags.PICKAXE_MINEABLE, BlockTags.NEEDS_STONE_TOOL)
            .linkChildEntry(() -> CONCRETE_POWDER)
            .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant TERRACOTTA = new DyeableBlockVariant(new Identifier("terracotta"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.CLEAR).requiresTool().strength(1.25F, 4.2F), dyeColorant);
    }).setDefaultEntry("terracotta")
        .addTags(BlockTags.TERRACOTTA, BlockTags.PICKAXE_MINEABLE, BlockTags.NEEDS_STONE_TOOL)
        .configureBlockItemVariant(dyeableItemVariant -> dyeableItemVariant.addTags((ItemTags.TERRACOTTA)))
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant WOOL = new DyeableBlockVariant(new Identifier("wool"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredBlock(AbstractBlock.Settings.of(Material.WOOL, MapColor.CLEAR).strength(0.8F).sounds(BlockSoundGroup.WOOL), dyeColorant);
    }).addTags(BlockTags.WOOL)
        .configureBlockItemVariant(dyeableItemVariant -> dyeableItemVariant.addTags(ItemTags.WOOL))
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant CARPET = new DyeableBlockVariant(new Identifier("carpet"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredCarpetBlock(AbstractBlock.Settings.of(Material.CARPET, MapColor.CLEAR).strength(0.1F).sounds(BlockSoundGroup.WOOL), dyeColorant);
    }).addTags(BlockTags.WOOL_CARPETS)
        .configureBlockItemVariant(dyeableItemVariant -> dyeableItemVariant.addTags(ItemTags.WOOL_CARPETS))
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant CANDLE_CAKE = new DyeableBlockVariant(new Identifier("candle_cake"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredCandleCakeBlock(dyeColorant, parentBlock, AbstractBlock.Settings.copy(Blocks.CANDLE_CAKE));
    }).setDefaultEntry("candle_cake")
        .setParentId(new Identifier("candle"))
        .addTags(BlockTags.CANDLE_CAKES)
        .noBlockItem()
        .setLootTable(block -> GelatinLootTables.candleCakeDrops((Block)block).build())
        .register();

    public static final DyeableBlockVariant CANDLE = new DyeableBlockVariant(new Identifier("candle"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredCandleBlock(dyeColorant, AbstractBlock.Settings.of(Material.DECORATION, MapColor.CLEAR).nonOpaque().strength(0.1F).sounds(BlockSoundGroup.CANDLE).luminance(CandleBlock.STATE_TO_LUMINANCE));
    }).setDefaultEntry("candle")
        .linkChildEntry(() -> CANDLE_CAKE)
        .addTags(BlockTags.CANDLES)
        .configureBlockItemVariant(dyeableItemVariant -> dyeableItemVariant.addTags(ItemTags.CANDLES))
        .setLootTable(block -> GelatinLootTables.candleDrops((Block)block).build())
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant BED = new DyeableBlockVariant(new Identifier("bed"), ItemFunctions.copyFrom(itemSettings).maxCount(1), (dyeColorant, parentBlock) -> {
        Block block = new ColoredBedBlock(dyeColorant,
                AbstractBlock.Settings.of(Material.WOOL, state -> state.get(BedBlock.PART) == BedPart.FOOT ? MapColor.CLEAR : MapColor.WHITE_GRAY).sounds(BlockSoundGroup.WOOD).strength(0.2F).nonOpaque());

        Registry.POINT_OF_INTEREST_TYPE.getOrThrow(PointOfInterestTypes.HOME).blockStates().addAll(block.getStateManager().getStates().stream()
            .filter(blockState -> blockState.get(BedBlock.PART) == BedPart.HEAD).toList());

        return addToBlockEntitieset(block, BlockEntityType.BED);
    }).setBlockStateChangeMethod(
        (world, blockPos, currentState, newBlock, player) -> {
            BlockState bedPart = world.getBlockState(blockPos);
            Direction facingDirection = BedBlock.getDirection(world, blockPos);

            if (bedPart.get(BedBlock.PART) == BedPart.HEAD) {
                blockPos = blockPos.offset(facingDirection.getOpposite());

                bedPart = world.getBlockState(blockPos);
            }

            if (!world.isClient) {
                BlockState changedState = newBlock.getDefaultState().with(HorizontalFacingBlock.FACING, bedPart.get(HorizontalFacingBlock.FACING));

                world.setBlockState(blockPos.offset(bedPart.get(HorizontalFacingBlock.FACING)), Blocks.AIR.getDefaultState());
                world.setBlockState(blockPos, changedState);

                changedState.getBlock().onPlaced(world, blockPos, changedState, player, ItemStack.EMPTY);
            }
            return true;
    }).addTags(BlockTags.BEDS)
        .configureBlockItemVariant(dyeableItemVariant -> dyeableItemVariant.addTags(ItemTags.BEDS))
        .setLootTable(block -> GelatinLootTables.dropsWithProperty((Block)block, BedBlock.PART, BedPart.HEAD).build())
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant GLASS = new DyeableBlockVariant(new Identifier("stained_glass"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredGlassBlock(dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
    }).setDefaultEntry("glass")
        .addTags(BlockTags.IMPERMEABLE)
        .setLootTable(block -> GelatinLootTables.dropsWithSilkTouch(block).build())
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant GLASS_PANE = new DyeableBlockVariant(new Identifier("stained_glass_pane"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredGlassPaneBlock(dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
    }).setDefaultEntry("glass_pane")
        .setLootTable(block -> GelatinLootTables.dropsWithSilkTouch(block).build())
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant SHULKER_BOX = new DyeableBlockVariant(new Identifier("shulker_box"), ItemFunctions.copyFrom(itemSettings).maxCount(1),
        (dyeColorant, parentBlock) -> {
            AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> !(world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) || shulkerBoxBlockEntity.suffocates();

            Block block = new ColoredShulkerBoxBlock(dyeColorant, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.CLEAR).strength(2.0F).dynamicBounds().nonOpaque().suffocates(contextPredicate).blockVision(contextPredicate));

            return addToBlockEntitieset(block, BlockEntityType.SHULKER_BOX);
        }).setBlockStateChangeMethod(
            (world, blockPos, currentState, newBlock, player) -> {
                if (world.getBlockEntity(blockPos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity &&
                        shulkerBoxBlockEntity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                    NbtCompound tag = new NbtCompound();

                    ((ShulkerBoxBlockEntityAccessor) shulkerBoxBlockEntity).gelatin$callWriteNbt(tag);

                    if (!world.isClient) {
                        world.setBlockState(blockPos, newBlock.getStateWithProperties(currentState));
                        world.getBlockEntity(blockPos).readNbt(tag);
                    }

                    return true;
                }

                return false;
        }).setDefaultEntry("shulker_box")
            .addTags(BlockTags.SHULKER_BOXES)
            .setLootTable(block -> GelatinLootTables.shulkerBoxDrops(block).build())
            .register();

    //-----------------------------------------------------------------

    public static final List<DyeableBlockVariant> ALL_VANILLA_VARIANTS =
            List.of(CONCRETE,
                    CONCRETE_POWDER,
                    TERRACOTTA,
                    WOOL,
                    CARPET,
                    CANDLE,
                    CANDLE_CAKE,
                    BED,
                    GLASS,
                    GLASS_PANE,
                    SHULKER_BOX);

    /**
     * Returns a list containing only the base vanilla variants without the recursive children
     */
    public static List<DyeableBlockVariant> getBaseVanillaBlockVariants(){
        return ALL_VANILLA_VARIANTS.stream().filter(dyeableBlockVariant -> dyeableBlockVariant.parentVariantIdentifier == null).collect(Collectors.toList());
    }

    private static Block addToBlockEntitieset(Block block, BlockEntityType<?> blockEntityType) {
        Set<Block> BLOCK_SET = new HashSet<>(((BlockEntityTypeAccessor) blockEntityType).gelatin$getBlocks());
        BLOCK_SET.add(block);
        ((BlockEntityTypeAccessor) blockEntityType).gelatin$setBlocks(BLOCK_SET);

        return block;
    }
}
