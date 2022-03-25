package io.wispforest.jello.api.dye.registry.builder;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.block.*;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import io.wispforest.jello.api.mixin.mixins.BlockEntityTypeAccessor;
import io.wispforest.jello.main.common.data.tags.JelloTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VanillaBlockBuilder extends BaseBlockBuilder {

    public VanillaBlockBuilder(List<BlockType> blockTypes, BlockBuildFunction baseBlockBuilder){
        super("jello", blockTypes, baseBlockBuilder);
    }

    public List<BlockType.RegistryHelper> build(DyeColorant dyeColorant, boolean readOnly){
        return this.baseBlockBuilder.createMultiBlockSet(blockTypes, dyeColorant, readOnly);
    }

    public static final VanillaBlockBuilder CONCRETE_VARS = new VanillaBlockBuilder(new BlockType.Builder().of("concrete", "minecraft", JelloTags.Blocks.CONCRETE).of("concrete_powder", "minecraft", JelloTags.Blocks.CONCRETE_POWDER).getTypes(),
            (blockTypes, dyeColorant, readOnly) -> {
                List<BlockType.RegistryHelper> CURRENT_SET = new ArrayList<>();

                Block parent = !readOnly ? new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.CLEAR).requiresTool().strength(1.8F), dyeColorant) : Blocks.AIR;
                CURRENT_SET.add(BlockType.createHelper(blockTypes.get(0), parent));

                Block ancestor1 = !readOnly ? new ColoredConcretePowderBlock(parent, AbstractBlock.Settings.of(Material.AGGREGATE, MapColor.CLEAR).strength(0.5F).sounds(BlockSoundGroup.SAND), dyeColorant) : Blocks.AIR;
                CURRENT_SET.add(BlockType.createHelper(blockTypes.get(1), ancestor1));

                return CURRENT_SET;
    });

    public static final VanillaBlockBuilder TERRACOTTA = new VanillaBlockBuilder(new BlockType.Builder().of("terracotta", new Identifier("terracotta"), BlockTags.TERRACOTTA).getTypes(), (blockTypes, dyeColorant, readOnly) -> {
        Block block = !readOnly ? new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.CLEAR).requiresTool().strength(1.25F, 4.2F), dyeColorant) : Blocks.AIR;

        return List.of(BlockType.createHelper(blockTypes.get(0), block));
    });

    public static final VanillaBlockBuilder WOOL = new VanillaBlockBuilder(new BlockType.Builder().of("wool", "minecraft", BlockTags.WOOL).getTypes(),  (blockTypes, dyeColorant, readOnly) -> {
        Block block = !readOnly ? new ColoredBlock(AbstractBlock.Settings.of(Material.WOOL, MapColor.CLEAR).strength(0.8F).sounds(BlockSoundGroup.WOOL), dyeColorant) : Blocks.AIR;

        return List.of(BlockType.createHelper(blockTypes.get(0), block));
    });

    public static final VanillaBlockBuilder CARPET = new VanillaBlockBuilder(new BlockType.Builder().of("carpet", "minecraft", BlockTags.CARPETS).getTypes(), (blockTypes, dyeColorant, readOnly) -> {
        Block block = !readOnly ? new ColoredCarpetBlock(AbstractBlock.Settings.of(Material.CARPET, MapColor.CLEAR).strength(0.1F).sounds(BlockSoundGroup.WOOL), dyeColorant) : Blocks.AIR;

        return List.of(BlockType.createHelper(blockTypes.get(0), block));
    });

    public static final VanillaBlockBuilder CANDLE_VARS = new VanillaBlockBuilder(new BlockType.Builder().of("candle", new Identifier("candle"), BlockTags.CANDLES, ItemTags.CANDLES).of("candle_cake", new Identifier("candle_cake"), BlockTags.CANDLE_CAKES).getTypes(), (blockTypes, dyeColorant, readOnly) -> {
        List<BlockType.RegistryHelper> CURRENT_SET = new ArrayList<>();

        Block parent = !readOnly ? new ColoredCandleBlock(dyeColorant, AbstractBlock.Settings.of(Material.DECORATION, MapColor.CLEAR).nonOpaque().strength(0.1F).sounds(BlockSoundGroup.CANDLE).luminance(CandleBlock.STATE_TO_LUMINANCE)) : Blocks.AIR;
        CURRENT_SET.add(BlockType.createHelper(blockTypes.get(0), parent));

        Block ancestor1 = !readOnly ? new ColoredCandleCakeBlock(dyeColorant, parent, AbstractBlock.Settings.copy(Blocks.CANDLE_CAKE)) : Blocks.AIR;
        CURRENT_SET.add(BlockType.createHelper(blockTypes.get(1), ancestor1,false));

        return CURRENT_SET;
    });

    public static final VanillaBlockBuilder BED = new VanillaBlockBuilder(new BlockType.Builder().of("bed","minecraft", BlockTags.BEDS).getTypes(), (blockTypes, dyeColorant, readOnly) -> {
        Set<Block> BED_VARS = new HashSet<>(((BlockEntityTypeAccessor) BlockEntityType.BED).jello$getBlocks());

        Block block = !readOnly ? new BedBlock(DyeColorantRegistry.Constants.NULL_VALUE_OLD,
                AbstractBlock.Settings.of(Material.WOOL, state -> state.get(BedBlock.PART) == BedPart.FOOT ? MapColor.CLEAR : MapColor.WHITE_GRAY).sounds(BlockSoundGroup.WOOD).strength(0.2F).nonOpaque()) : Blocks.AIR;

        if(!readOnly) {
            ((DyeBlockStorage) block).setDyeColor(dyeColorant);
            BED_VARS.add(block);

            ((BlockEntityTypeAccessor) BlockEntityType.BED).jello$setBlocks(BED_VARS);
        }

        return List.of(BlockType.createHelper(blockTypes.get(0), block));
    });

    public static final VanillaBlockBuilder GLASS = new VanillaBlockBuilder(new BlockType.Builder().of("stained_glass", new Identifier("glass"), JelloTags.Blocks.STAINED_GLASS).getTypes(), (blockTypes, dyeColorant, readOnly) -> {
        Block block = !readOnly ? new ColoredGlassBlock(dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()) : Blocks.AIR;

        return List.of(BlockType.createHelper(blockTypes.get(0), block));
    });

    public static final VanillaBlockBuilder GLASS_PANE = new VanillaBlockBuilder(new BlockType.Builder().of("stained_glass_pane", new Identifier("glass_pane"), JelloTags.Blocks.GLASS_PANES).getTypes(), (blockTypes, dyeColorant, readOnly) -> {
        Block block = !readOnly ? new ColoredGlassPaneBlock(dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque()) : Blocks.AIR;

        return List.of(BlockType.createHelper(blockTypes.get(0), block));
    });

    public static final VanillaBlockBuilder SHULKER_BOX = new VanillaBlockBuilder(new BlockType.Builder().of("shulker_box", "minecraft", BlockTags.SHULKER_BOXES).getTypes(), (blockTypes, dyeColorant, readOnly) -> {
        Set<Block> SHULKER_VARS = new HashSet<>(((BlockEntityTypeAccessor) BlockEntityType.SHULKER_BOX).jello$getBlocks());

        AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> !(world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) || shulkerBoxBlockEntity.suffocates();
        Block block = !readOnly ? new ShulkerBoxBlock(DyeColorantRegistry.Constants.NULL_VALUE_OLD, AbstractBlock.Settings.of(Material.SHULKER_BOX, MapColor.CLEAR).strength(2.0F).dynamicBounds().nonOpaque().suffocates(contextPredicate).blockVision(contextPredicate)) : Blocks.AIR;

        if(!readOnly) {
            ((DyeBlockStorage) block).setDyeColor(dyeColorant);
            SHULKER_VARS.add(block);

            ((BlockEntityTypeAccessor) BlockEntityType.SHULKER_BOX).jello$setBlocks(SHULKER_VARS);
        }

        return List.of(BlockType.createHelper(blockTypes.get(0), block));
    });

    public static List<VanillaBlockBuilder> VANILLA_BUILDERS = List.of(
            CONCRETE_VARS,
            TERRACOTTA,
            WOOL,
            CARPET,
            CANDLE_VARS,
            BED,
            GLASS,
            GLASS_PANE,
            SHULKER_BOX);

    public static void init(){

    }
}
