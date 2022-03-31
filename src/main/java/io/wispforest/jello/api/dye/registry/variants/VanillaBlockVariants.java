package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.block.colored.*;
import io.wispforest.jello.misc.ducks.DyeBlockStorage;
import io.wispforest.jello.mixin.accessors.BlockEntityTypeAccessor;
import io.wispforest.jello.data.tags.JelloTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VanillaBlockVariants {

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant CONCRETE_POWDER = DyeableBlockVariant.of(new Identifier("concrete_powder"), (dyeColorant, parentBlock) -> {
        return new ColoredConcretePowderBlock(parentBlock, AbstractBlock.Settings.of(Material.AGGREGATE, MapColor.CLEAR).strength(0.5F).sounds(BlockSoundGroup.SAND), dyeColorant);
    }).setBlockTags(JelloTags.Blocks.CONCRETE_POWDER);

    public static final DyeableBlockVariant CONCRETE = DyeableBlockVariant.of(new Identifier("concrete"), () -> CONCRETE_POWDER, (dyeColorant, parentBlock) -> {
        return new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.CLEAR).requiresTool().strength(1.8F), dyeColorant);
    }).setBlockTags(JelloTags.Blocks.CONCRETE);

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant TERRACOTTA = DyeableBlockVariant.of(new Identifier("terracotta"), (dyeColorant, parentBlock) -> {
        return new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.CLEAR).requiresTool().strength(1.25F, 4.2F), dyeColorant);
    }).setBlockTags(BlockTags.TERRACOTTA).setDefaultBlock("terracotta");

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant WOOL = DyeableBlockVariant.of(new Identifier("wool"), (dyeColorant, parentBlock) -> {
        return new ColoredBlock(AbstractBlock.Settings.of(Material.WOOL, MapColor.CLEAR).strength(0.8F).sounds(BlockSoundGroup.WOOL), dyeColorant);
    }).setBlockTags(BlockTags.WOOL).setItemTags(JelloTags.Items.WOOL);

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant CARPET = DyeableBlockVariant.of(new Identifier("carpet"), (dyeColorant, parentBlock) -> {
        return new ColoredCarpetBlock(AbstractBlock.Settings.of(Material.CARPET, MapColor.CLEAR).strength(0.1F).sounds(BlockSoundGroup.WOOL), dyeColorant);
    }).setBlockTags(BlockTags.CARPETS);

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant CANDLE_CAKE = DyeableBlockVariant.of(new Identifier("candle_cake"), false, (dyeColorant, parentBlock) -> {
        return new ColoredCandleCakeBlock(dyeColorant, parentBlock, AbstractBlock.Settings.copy(Blocks.CANDLE_CAKE));
    }).setBlockTags(BlockTags.CANDLE_CAKES).setDefaultBlock("candle_cake");

    public static final DyeableBlockVariant CANDLE = DyeableBlockVariant.of(new Identifier("candle"), () -> CANDLE_CAKE, (dyeColorant, parentBlock) -> {
        return new ColoredCandleBlock(dyeColorant, AbstractBlock.Settings.of(Material.DECORATION, MapColor.CLEAR).nonOpaque().strength(0.1F).sounds(BlockSoundGroup.CANDLE).luminance(CandleBlock.STATE_TO_LUMINANCE));
    }).setBlockTags(BlockTags.CANDLES).setItemTags(ItemTags.CANDLES).setDefaultBlock("candle");

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant BED = DyeableBlockVariant.of(new Identifier("bed"), (dyeColorant, parentBlock) -> {
        Block block = new BedBlock(DyeColorantRegistry.Constants.NULL_VALUE_OLD,
                AbstractBlock.Settings.of(Material.WOOL, state -> state.get(BedBlock.PART) == BedPart.FOOT ? MapColor.CLEAR : MapColor.WHITE_GRAY).sounds(BlockSoundGroup.WOOD).strength(0.2F).nonOpaque());
        ((DyeBlockStorage) block).setDyeColor(dyeColorant);

        return addToBlockEntitieset(block, BlockEntityType.BED);
    }).setBlockTags(BlockTags.BEDS);

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant GLASS = DyeableBlockVariant.of(new Identifier("stained_glass"), (dyeColorant, parentBlock) -> {
        return new ColoredGlassBlock(dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
    }).setBlockTags(JelloTags.Blocks.STAINED_GLASS, BlockTags.IMPERMEABLE).setDefaultBlock("glass");

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant GLASS_PANE = DyeableBlockVariant.of(new Identifier("stained_glass_pane"), (dyeColorant, parentBlock) -> {
        return new ColoredGlassPaneBlock(dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
    }).setBlockTags(JelloTags.Blocks.GLASS_PANES).setDefaultBlock("glass_pane");

    //-----------------------------------------------------------------

    public static final DyeableBlockVariant SHULKER_BOX = DyeableBlockVariant.of(new Identifier("shulker_box"), (dyeColorant, parentBlock) -> {
        AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> !(world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) || shulkerBoxBlockEntity.suffocates();

        Block block = new ShulkerBoxBlock(DyeColorantRegistry.Constants.NULL_VALUE_OLD, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.CLEAR).strength(2.0F).dynamicBounds().nonOpaque().suffocates(contextPredicate).blockVision(contextPredicate));
        ((DyeBlockStorage) block).setDyeColor(dyeColorant);

        return addToBlockEntitieset(block, BlockEntityType.SHULKER_BOX);
    }).setBlockTags(BlockTags.SHULKER_BOXES).stackCount(1).setBlockItemMaker((dyeColorant, block, settings) -> new BlockItem(block, settings)).setDefaultBlock("shulker_box");

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
