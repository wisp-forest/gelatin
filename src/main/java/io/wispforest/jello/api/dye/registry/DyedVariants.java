package io.wispforest.jello.api.dye.registry;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.block.*;
import io.wispforest.jello.api.dye.item.DyeItem;
import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import io.wispforest.jello.api.dye.item.ColoredBlockItem;
import io.wispforest.jello.api.mixin.mixins.BlockEntityTypeAccessor;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class DyedVariants {

    public static Map<DyeColorant, DyedVariants> DYE_ITEM_VARIANTS = new HashMap<>();

    public List<Item> dyedItems;
    public List<Block> dyedBlocks;

    public DyeColorant dyeColorant;

    protected DyedVariants(List<Item> dyedItems, List<Block> dyedBlocks, DyeColorant dyeColorant){
        this.dyedItems = dyedItems;
        this.dyedBlocks = dyedBlocks;
        this.dyeColorant = dyeColorant;
    }

    @Override
    public String toString() {
        return "[ " + dyeColorant.toString() + " / " + dyedBlocks.toString() + " / " + dyedItems.toString() + " ]";
    }

    public static class Builder {

        private static final OwoItemSettings BASE_BLOCK_ITEM_SETTINGS = new OwoItemSettings().group(ItemGroup.MISC).tab(2);
        private static final OwoItemSettings BASE_ITEM_SETTINGS = new OwoItemSettings().group(ItemGroup.MISC).tab(3);

        public DyeColorant dyeColorant;

        private final BlockBuilder blockBuilder;
        private final ItemBuilder itemBuilder;

        public Builder(DyeColorant dyeColorant, Item.Settings settings){
            this(dyeColorant, settings, settings, dyeColorant.getId().getNamespace());
        }

        public Builder(DyeColorant dyeColorant, Item.Settings itemSettings, Item.Settings blockItemSettings){
            this(dyeColorant, itemSettings, blockItemSettings, dyeColorant.getId().getNamespace());
        }

        public Builder(DyeColorant dyeColorant, Item.Settings settings, String modid){
            this(dyeColorant, settings, settings, modid);
        }

        public Builder(DyeColorant dyeColorant, Item.Settings itemSettings, Item.Settings blockItemSettings, String modid){
            this.blockBuilder = new BlockBuilder(dyeColorant, blockItemSettings, modid);
            this.itemBuilder = new ItemBuilder(dyeColorant, itemSettings, modid);

            this.dyeColorant = dyeColorant;
        }

        @ApiStatus.Internal
        protected Builder(DyeColorant dyeColorant, String modid){
            this(dyeColorant, BASE_ITEM_SETTINGS, BASE_BLOCK_ITEM_SETTINGS, modid);
        }

        public DyedVariants createColoredVariants(boolean useJelloAPIModelRedirect){
            List<Block> blockVariants = this.blockBuilder.createBlockVariants(useJelloAPIModelRedirect);
            List<Item> itemVariants = this.itemBuilder.createItemVariants(useJelloAPIModelRedirect);

            DyedVariants dyedVariant = new DyedVariants(itemVariants, blockVariants, this.dyeColorant);
            DYE_ITEM_VARIANTS.put(this.dyeColorant, dyedVariant);

            return dyedVariant;
        }

        @ApiStatus.Internal
        protected DyedVariants createColoredVariants(Item.Settings dyeItemSettings){
            List<Block> blockVariants = this.blockBuilder.createBlockVariants(true);
            List<Item> itemVariants = this.itemBuilder.createItemVariants(dyeItemSettings, true);

            DyedVariants dyedVariant = new DyedVariants(itemVariants, blockVariants, this.dyeColorant);
            DYE_ITEM_VARIANTS.put(this.dyeColorant, dyedVariant);

            return dyedVariant;
        }

        private static class BlockBuilder {

            private final DyeColorant dyeColorant;
            private final Item.Settings settings;
            private final String modid;

            private boolean useJelloAPIModelRedirect = true;

            private List<Block> BLOCK_VARS;

            private BlockBuilder(DyeColorant dyeColorant, Item.Settings settings, String modid){
                this.dyeColorant = dyeColorant;
                this.settings = settings;
                this.modid = modid;
            }

            private Block createConcreteVariant() {
                Block block = new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, DyeColorantRegistry.Constants.NULL_VALUE_OLD).requiresTool().strength(1.8F), this.dyeColorant);
                BLOCK_VARS.add(block);

                return registerBlock("_concrete", block);
            }

            private Block createConcretePowderVariant(Block concreteBlock) {
                Block block = new ColoredConcretePowderBlock(concreteBlock, AbstractBlock.Settings.of(Material.AGGREGATE, DyeColorantRegistry.Constants.NULL_VALUE_OLD).strength(0.5F).sounds(BlockSoundGroup.SAND), this.dyeColorant);
                BLOCK_VARS.add(block);

                return registerBlock("_concrete_powder", block);
            }

            private Block createTerracottaVariant() {
                Block block = new ColoredBlock(AbstractBlock.Settings.of(Material.STONE, DyeColorantRegistry.Constants.NULL_VALUE_OLD).requiresTool().strength(1.25F, 4.2F), this.dyeColorant);
                BLOCK_VARS.add(block);

                return registerBlock("_terracotta", block);
            }

            private Block createWoolVariant() {
                Block block = new ColoredBlock(AbstractBlock.Settings.of(Material.WOOL, DyeColorantRegistry.Constants.NULL_VALUE_OLD).strength(0.8F).sounds(BlockSoundGroup.WOOL), this.dyeColorant);
                BLOCK_VARS.add(block);

                return registerBlock("_wool", block);
            }

            private Block createCarpetVariant() {
                Block block = new ColoredCarpetBlock(AbstractBlock.Settings.of(Material.CARPET, DyeColorantRegistry.Constants.NULL_VALUE_OLD).strength(0.1F).sounds(BlockSoundGroup.WOOL), this.dyeColorant);
                BLOCK_VARS.add(block);

                return registerBlock("_carpet", block);
            }

            public Block createCandleVariant(){
                Block block = new ColoredCandleBlock(this.dyeColorant ,AbstractBlock.Settings.of(Material.DECORATION, DyeColorantRegistry.Constants.NULL_VALUE_OLD).nonOpaque().strength(0.1F).sounds(BlockSoundGroup.CANDLE).luminance(CandleBlock.STATE_TO_LUMINANCE));
                BLOCK_VARS.add(block);

                return registerBlock("_candle", block);
            }

            public Block createCandleCakeVariant(Block candleBlock){
                Block block = new ColoredCandleCakeBlock(this.dyeColorant, candleBlock, AbstractBlock.Settings.copy(Blocks.CANDLE_CAKE));
                BLOCK_VARS.add(block);

                return registerBlock("_candle_cake", block, false);
            }

            private Block createBedVariant() {
                Set<Block> BED_VARS = new HashSet<>(((BlockEntityTypeAccessor) BlockEntityType.BED).jello$getBlocks());

                Block block = new BedBlock(DyeColorantRegistry.Constants.NULL_VALUE_OLD,
                        AbstractBlock.Settings.of(Material.WOOL, state -> state.get(BedBlock.PART) == BedPart.FOOT ? MapColor.CLEAR : MapColor.WHITE_GRAY).sounds(BlockSoundGroup.WOOD).strength(0.2F).nonOpaque());
                ((DyeBlockStorage)block).setDyeColor(this.dyeColorant);
                BLOCK_VARS.add(block);
                BED_VARS.add(block);

                ((BlockEntityTypeAccessor) BlockEntityType.BED).jello$setBlocks(BED_VARS);

                return registerBlock("_bed", block);
            }

            public Block createGlassVariant(){
                Block block = new ColoredGlassBlock(this.dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
                BLOCK_VARS.add(block);

                return registerBlock("_glass", block);
            }

            public Block createGlassPaneVariant(){
                Block block = new ColoredGlassPaneBlock(this.dyeColorant, AbstractBlock.Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS).nonOpaque());
                BLOCK_VARS.add(block);

                return registerBlock("_glass_pane", block);
            }

            private Block createShulkerVariant() {
                Set<Block> SHULKER_VARS = new HashSet<>(((BlockEntityTypeAccessor) BlockEntityType.SHULKER_BOX).jello$getBlocks());

                Block block = createShulkerBoxBlock(AbstractBlock.Settings.of(Material.SHULKER_BOX, DyeColorantRegistry.Constants.NULL_VALUE_OLD));
                ((DyeBlockStorage) block).setDyeColor(this.dyeColorant);
                BLOCK_VARS.add(block);
                SHULKER_VARS.add(block);

                ((BlockEntityTypeAccessor) BlockEntityType.SHULKER_BOX).jello$setBlocks(SHULKER_VARS);

                return registerBlock("_shulker_box", block);
            }

            private ShulkerBoxBlock createShulkerBoxBlock(AbstractBlock.Settings settings) {
                AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (!(blockEntity instanceof ShulkerBoxBlockEntity)) {
                        return true;
                    } else {
                        ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity) blockEntity;
                        return shulkerBoxBlockEntity.suffocates();
                    }
                };
                return new ShulkerBoxBlock(DyeColorantRegistry.Constants.NULL_VALUE_OLD, settings.strength(2.0F).dynamicBounds().nonOpaque().suffocates(contextPredicate).blockVision(contextPredicate));
            }

            public List<Block> createBlockVariants(boolean useJelloAPIModelRedirect) {
                BLOCK_VARS = new ArrayList<>();
                this.useJelloAPIModelRedirect = useJelloAPIModelRedirect;

                Block concreteBlock = createConcreteVariant();
                Block concretePowderBlock = createConcretePowderVariant(concreteBlock);
                Block terracottaBlock = createTerracottaVariant();
                Block woolBlock = createWoolVariant();
                Block carpetBlock = createCarpetVariant();
                Block glassBlock = createGlassVariant();
                Block glassPaneBlock = createGlassPaneVariant();
                Block bedBlock = createBedVariant();

                Block candleBlock = createCandleVariant();
                TagInjector.injectItems(ItemTags.CANDLES.id(), candleBlock.asItem());
                TagInjector.injectBlocks(BlockTags.CANDLES.id(), candleBlock);

                Block candleCakeBlock = createCandleCakeVariant(candleBlock);
                TagInjector.injectBlocks(BlockTags.CANDLE_CAKES.id(), candleCakeBlock);

                Block shulkerBlock = createShulkerVariant();

                return BLOCK_VARS;
            }

            private Block registerBlock(String suffix, Block block) {
                return registerBlock(suffix, block, true);
            }

            private Block registerBlock(String suffix, Block block, boolean createBlockItem) {
                Identifier identifier = new Identifier(this.modid, this.dyeColorant.getId().getPath() + suffix);

                if(this.useJelloAPIModelRedirect){
                    DyeColorantRegistry.IDENTIFIER_RESOURCE_REDIRECTS.add(identifier);
                }

                if(createBlockItem) {
                    if (!(block instanceof ShulkerBoxBlock)) {
                        Registry.register(Registry.ITEM, identifier, new ColoredBlockItem(block, this.settings));
                    } else {
                        Registry.register(Registry.ITEM, identifier, new BlockItem(block, this.settings.maxCount(1)));
                    }
                }

                return Registry.register(Registry.BLOCK, identifier, block);
            }
        }

        private static class ItemBuilder {

            private final DyeColorant dyeColorant;
            private final Item.Settings settings;
            private final String modid;

            private boolean useJelloAPIModelRedirect = true;

            private List<Item> itemVariants;

            private ItemBuilder(DyeColorant dyeColorant, Item.Settings settings, String modid){
                this.dyeColorant = dyeColorant;
                this.settings = settings;
                this.modid = modid;
            }

            private DyeItem createDyeItem() {
                return this.createDyeItem(this.settings);
            }

            private DyeItem createDyeItem(Item.Settings settings){
                DyeItem dyeItem = (DyeItem) register("_dye", new DyeItem(this.dyeColorant, settings));

                itemVariants.add(dyeItem);
                return dyeItem;
            }

            @ApiStatus.Internal
            private List<Item> createItemVariants(Item.Settings settings, boolean useJelloAPIModelRedirect){
                itemVariants = new ArrayList<>();

                Item DyeItem = createDyeItem(settings);

                return itemVariants;
            }

            private List<Item> createItemVariants(boolean useJelloAPIModelRedirect){
                itemVariants = new ArrayList<>();
                this.useJelloAPIModelRedirect = useJelloAPIModelRedirect;

                Item DyeItem = createDyeItem();

                return itemVariants;
            }

            private Item register(String suffix, Item item){
                Identifier id = new Identifier(this.modid ,dyeColorant.getId().getPath() + suffix);

                if(this.useJelloAPIModelRedirect){
                    DyeColorantRegistry.IDENTIFIER_RESOURCE_REDIRECTS.add(id);
                }

                return Registry.register(Registry.ITEM, id, item);
            }
        }
    }

}
