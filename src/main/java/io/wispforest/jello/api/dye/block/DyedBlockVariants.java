package io.wispforest.jello.api.dye.block;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.DyeColorantJsonTest;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import io.wispforest.jello.api.dye.item.MultiColorBlockItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class DyedBlockVariants {

    private static final OwoItemSettings BASE_ITEM_SETTINGS = new OwoItemSettings().group(ItemGroup.MISC).tab(2);

    public static Block createConcreteVariant(DyeColorant dyeColorant, List<Block> blockVarList){
        Block block = new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, DyeColorRegistry.NULL_VALUE_OLD).requiresTool().strength(1.8F), dyeColorant);
        blockVarList.add(block);

        return registerBlock(new Identifier(DyeColorantJsonTest.JSON_NAMESPACE, dyeColorant.getId().getPath() + "_concrete"), block);
    }

    public static Block createConcretePowderVariant(Block concreteBlock, DyeColorant dyeColorant, List<Block> blockVarList){
        Block block =  new ColoredConcretePowderBlock(concreteBlock, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColorRegistry.NULL_VALUE_OLD).strength(0.5F).sounds(BlockSoundGroup.SAND), dyeColorant);
        blockVarList.add(block);

        return registerBlock(new Identifier(DyeColorantJsonTest.JSON_NAMESPACE, dyeColorant.getId().getPath() + "_concrete_powder"), block);
    }

    public static Block createTerracottaVariant(DyeColorant dyeColorant, List<Block> blockVarList){
        Block block = new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, DyeColorRegistry.NULL_VALUE_OLD).requiresTool().strength(1.25F, 4.2F), dyeColorant);
        blockVarList.add(block);

        return registerBlock(new Identifier(DyeColorantJsonTest.JSON_NAMESPACE, dyeColorant.getId().getPath() + "_terracotta"), block);
    }

    public static Block createWoolVariant(DyeColorant dyeColorant, List<Block> blockVarList){
        Block block = new ColoredBlock(AbstractBlock.Settings.of(Material.WOOL, DyeColorRegistry.NULL_VALUE_OLD).strength(0.8F).sounds(BlockSoundGroup.WOOL), dyeColorant);
        blockVarList.add(block);

        return registerBlock(new Identifier(DyeColorantJsonTest.JSON_NAMESPACE, dyeColorant.getId().getPath() + "_wool"), block);
    }

    public static Block createCarpetVariant(DyeColorant dyeColorant, List<Block> blockVarList){
        Block block = new ColoredCarpetBlock(AbstractBlock.Settings.of(Material.CARPET, DyeColorRegistry.NULL_VALUE_OLD).strength(0.1F).sounds(BlockSoundGroup.WOOL), dyeColorant);
        blockVarList.add(block);

        return registerBlock(new Identifier(DyeColorantJsonTest.JSON_NAMESPACE, dyeColorant.getId().getPath() + "_carpet"), block);
    }

//    public static Block createBannerVariant(DyeColorant dyeColorant){
//        Block block = new BannerBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD));
//        ((DyeBlockStorage)block).setDyeColor(dyeColorant);
//
//        return block;
//    }
//
//    public static Block createWallBannerVariant(Block bannerBlock, DyeColorant dyeColorant){
//        Block block = new WallBannerBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(bannerBlock));
//        ((DyeBlockStorage)block).setDyeColor(dyeColorant);
//
//        return block;
//    }

//    public static Block createCandleVariant(DyeColorant dyeColorant){
//        Block block = new CandleBlock(
//                AbstractBlock.Settings.of(Material.DECORATION, DyeColorRegistry.NULL_VALUE_OLD)
//                        .nonOpaque()
//                        .strength(0.1F)
//                        .sounds(BlockSoundGroup.CANDLE)
//                        .luminance(CandleBlock.STATE_TO_LUMINANCE)
//        );
//        ((DyeBlockStorage)block).setDyeColor(dyeColorant);
//
//        return block;
//    }
//
//    public static Block createCandleCakeVariant(Block candleBlock, DyeColorant dyeColorant){
//        Block block = new CandleCakeBlock(candleBlock, AbstractBlock.Settings.copy(Blocks.CANDLE_CAKE));
//        ((DyeBlockStorage)block).setDyeColor(dyeColorant);
//
//        return block;
//    }
//
//    public static Block createGlassVaraint(DyeColorant dyeColorant){
//        Block block = new StainedGlassBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
//        ((DyeBlockStorage)block).setDyeColor(dyeColorant);
//
//        return block;
//    }
//
//    public static Block createGlassPaneVaraint(DyeColorant dyeColorant){
//        Block block = new StainedGlassPaneBlock(DyeColor.WHITE, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
//        ((DyeBlockStorage)block).setDyeColor(dyeColorant);
//
//        return block;
//    }

    public static Block createShulkerVariant(DyeColorant dyeColorant, List<Block> blockVarList){
        Block block = createShulkerBoxBlock(AbstractBlock.Settings.of(Material.SHULKER_BOX, DyeColorRegistry.NULL_VALUE_OLD));
        ((DyeBlockStorage)block).setDyeColor(dyeColorant);
        blockVarList.add(block);

        return registerBlock(new Identifier(DyeColorantJsonTest.JSON_NAMESPACE, dyeColorant.getId().getPath() + "_shulker_box"), block);
    }

    private static ShulkerBoxBlock createShulkerBoxBlock(AbstractBlock.Settings settings) {
        AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof ShulkerBoxBlockEntity)) {
                return true;
            } else {
                ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
                return shulkerBoxBlockEntity.suffocates();
            }
        };
        return new ShulkerBoxBlock(DyeColorRegistry.NULL_VALUE_OLD, settings.strength(2.0F).dynamicBounds().nonOpaque().suffocates(contextPredicate).blockVision(contextPredicate));
    }

    public static List<Block> createBlockVariants(DyeColorant dyeColorant){
        List<Block> BLOCK_VARS = new ArrayList<>();

        Block concreteBlock = createConcreteVariant(dyeColorant, BLOCK_VARS);
        Block concretePowderBlock = createConcretePowderVariant(concreteBlock, dyeColorant, BLOCK_VARS);
        Block terracottaBlock = createTerracottaVariant(dyeColorant, BLOCK_VARS);
        Block woolBlock = createWoolVariant(dyeColorant, BLOCK_VARS);
        Block carpetBlock = createCarpetVariant(dyeColorant, BLOCK_VARS);
        Block shulkerBlock = createShulkerVariant(dyeColorant, BLOCK_VARS);

        return BLOCK_VARS;
    }

    private static Block registerBlock(Identifier identifier, Block block){
        if(!(block instanceof ShulkerBoxBlock)){
            Registry.register(Registry.ITEM, identifier, new MultiColorBlockItem(block, BASE_ITEM_SETTINGS));
        }else{
            Registry.register(Registry.ITEM, identifier, new BlockItem(block, BASE_ITEM_SETTINGS));
        }

        return Registry.register(Registry.BLOCK, identifier, block);
    }

}
