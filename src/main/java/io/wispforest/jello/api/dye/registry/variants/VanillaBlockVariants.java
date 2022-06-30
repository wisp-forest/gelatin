package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import io.wispforest.jello.api.item.JelloItemSettings;
import io.wispforest.jello.block.colored.*;
import io.wispforest.jello.data.loot.JelloLootTables;
import io.wispforest.jello.mixin.accessors.BlockEntityTypeAccessor;
import io.wispforest.jello.mixin.accessors.ShulkerBoxBlockEntityAccessor;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An implementation of all vanilla Dyeable blocks used when a {@link DyeColorant} is registered for Variant creation
 */
public class VanillaBlockVariants {

    private static final Item.Settings itemSettings = new OwoItemSettings()
            .group(Jello.MAIN_ITEM_GROUP)
            .tab(1)
            .maxCount(64);

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant CONCRETE_POWDER = DyeableBlockVariant.Builder.of(new Identifier("concrete_powder"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredConcretePowderBlock(parentBlock, AbstractBlock.Settings.of(Material.AGGREGATE, MapColor.CLEAR).strength(0.5F).sounds(BlockSoundGroup.SAND), dyeColorant);
    }).setBlockTags(BlockTags.PICKAXE_MINEABLE, BlockTags.NEEDS_STONE_TOOL)
            .register();

    public static final DyeableBlockVariant CONCRETE = DyeableBlockVariant.Builder.of(new Identifier("concrete"), itemSettings, () -> CONCRETE_POWDER, (dyeColorant, parentBlock) -> {
        return new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.CLEAR).requiresTool().strength(1.8F), dyeColorant);
    }).setBlockTags(BlockTags.PICKAXE_MINEABLE, BlockTags.NEEDS_STONE_TOOL)
            .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant TERRACOTTA = DyeableBlockVariant.Builder.of(new Identifier("terracotta"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.CLEAR).requiresTool().strength(1.25F, 4.2F), dyeColorant);
    }).setDefaultBlock("terracotta")
        .setBlockTags(BlockTags.TERRACOTTA, BlockTags.PICKAXE_MINEABLE, BlockTags.NEEDS_STONE_TOOL)
        .setItemTags(ItemTags.TERRACOTTA)
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant WOOL = DyeableBlockVariant.Builder.of(new Identifier("wool"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredBlock(AbstractBlock.Settings.of(Material.WOOL, MapColor.CLEAR).strength(0.8F).sounds(BlockSoundGroup.WOOL), dyeColorant);
    }).setBlockTags(BlockTags.WOOL)
        .setItemTags(ItemTags.WOOL)
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant CARPET = DyeableBlockVariant.Builder.of(new Identifier("carpet"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredCarpetBlock(AbstractBlock.Settings.of(Material.CARPET, MapColor.CLEAR).strength(0.1F).sounds(BlockSoundGroup.WOOL), dyeColorant);
    }).setBlockTags(BlockTags.WOOL_CARPETS)
        .setItemTags(ItemTags.WOOL_CARPETS)
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant CANDLE_CAKE = DyeableBlockVariant.Builder.of(new Identifier("candle_cake"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredCandleCakeBlock(dyeColorant, parentBlock, AbstractBlock.Settings.copy(Blocks.CANDLE_CAKE));
    }).setDefaultBlock("candle_cake")
        .setBlockTags(BlockTags.CANDLE_CAKES)
        .noBlockItem()
        .setLootTable(block -> JelloLootTables.candleCakeDrops(block).build())
        .register();

    public static final DyeableBlockVariant CANDLE = DyeableBlockVariant.Builder.of(new Identifier("candle"), itemSettings, () -> CANDLE_CAKE, (dyeColorant, parentBlock) -> {
        return new ColoredCandleBlock(dyeColorant, AbstractBlock.Settings.of(Material.DECORATION, MapColor.CLEAR).nonOpaque().strength(0.1F).sounds(BlockSoundGroup.CANDLE).luminance(CandleBlock.STATE_TO_LUMINANCE));
    }).setDefaultBlock("candle")
        .setBlockTags(BlockTags.CANDLES)
        .setItemTags(ItemTags.CANDLES)
        .setLootTable(block -> JelloLootTables.candleDrops(block).build())
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant BED = DyeableBlockVariant.Builder.of(new Identifier("bed"), itemSettings, (dyeColorant, parentBlock) -> {
        Block block = new ColoredBedBlock(dyeColorant,
                AbstractBlock.Settings.of(Material.WOOL, state -> state.get(BedBlock.PART) == BedPart.FOOT ? MapColor.CLEAR : MapColor.WHITE_GRAY).sounds(BlockSoundGroup.WOOD).strength(0.2F).nonOpaque());

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
    }).setBlockTags(BlockTags.BEDS)
        .setItemTags(ItemTags.BEDS)
        .setLootTable(block -> JelloLootTables.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD).build())
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant GLASS = DyeableBlockVariant.Builder.of(new Identifier("stained_glass"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredGlassBlock(dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
    }).setDefaultBlock("glass")
        .setBlockTags(BlockTags.IMPERMEABLE)
        .setLootTable(block -> JelloLootTables.dropsWithSilkTouch(block).build())
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant GLASS_PANE = DyeableBlockVariant.Builder.of(new Identifier("stained_glass_pane"), itemSettings, (dyeColorant, parentBlock) -> {
        return new ColoredGlassPaneBlock(dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
    }).setDefaultBlock("glass_pane")
        .setLootTable(block -> JelloLootTables.dropsWithSilkTouch(block).build())
        .register();

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant SHULKER_BOX = DyeableBlockVariant.Builder.of(new Identifier("shulker_box"), JelloItemSettings.copyFrom(itemSettings).maxCount(1),
        (dyeColorant, parentBlock) -> {
            AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> !(world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) || shulkerBoxBlockEntity.suffocates();

            Block block = new ColoredShulkerBoxBlock(dyeColorant, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.CLEAR).strength(2.0F).dynamicBounds().nonOpaque().suffocates(contextPredicate).blockVision(contextPredicate));

            return addToBlockEntitieset(block, BlockEntityType.SHULKER_BOX);
        }).setBlockStateChangeMethod(
            (world, blockPos, currentState, newBlock, player) -> {
                if (world.getBlockEntity(blockPos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity &&
                        shulkerBoxBlockEntity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                    NbtCompound tag = new NbtCompound();

                    ((ShulkerBoxBlockEntityAccessor) shulkerBoxBlockEntity).callWriteNbt(tag);

                    if (!world.isClient) {
                        world.setBlockState(blockPos, newBlock.getStateWithProperties(currentState));
                        world.getBlockEntity(blockPos).readNbt(tag);
                    }

                    return true;
                }

                return false;
        }).setDefaultBlock("shulker_box")
            .setBlockTags(BlockTags.SHULKER_BOXES)
            .setBlockItemMaker((dyeColorant, block, settings) -> new BlockItem(block, settings))
            .setLootTable(block -> JelloLootTables.shulkerBoxDrops(block).build())
            .register();

    //-----------------------------------------------------------------

    public static final List<DyeableBlockVariant> VANILLA_VARIANTS =
            List.of(CONCRETE,
                    TERRACOTTA,
                    WOOL,
                    CARPET,
                    CANDLE,
                    BED,
                    GLASS,
                    GLASS_PANE,
                    SHULKER_BOX);

    private static Block addToBlockEntitieset(Block block, BlockEntityType<?> blockEntityType) {
        Set<Block> BLOCK_SET = new HashSet<>(((BlockEntityTypeAccessor) blockEntityType).jello$getBlocks());
        BLOCK_SET.add(block);
        ((BlockEntityTypeAccessor) blockEntityType).jello$setBlocks(BLOCK_SET);

        return block;
    }
}
