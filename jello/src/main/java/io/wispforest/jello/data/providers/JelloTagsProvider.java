package io.wispforest.jello.data.providers;

import io.wispforest.dye_entries.variants.DyeableVariantManager;
import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;
import io.wispforest.dye_registry.data.GelatinTags;
import io.wispforest.jello.data.JelloTags;
import io.wispforest.jello.item.JelloItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

import java.util.Map;

public class JelloTagsProvider {

    public static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {

        public BlockTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {

//            for(DyeableBlockVariant blockVariant : DyeableBlockVariant.getAllVariants()){
//                Set<Block> blockSet = new HashSet<>();
//                for(DyeColorant dyeColorant : DyeColorantRegistry.DYE_COLOR){
//                    Block block = blockVariant.getColoredBlock(dyeColorant);
//                    blockSet.add(block);
//                }
//
//                this.getOrCreateTagBuilder(blockVariant.primaryBlockTag)
//                        .add(blockSet.toArray(new Block[]{}));
//
//                this.getOrCreateTagBuilder(blockVariant.getCommonBlockTag()).addTag(blockVariant.primaryBlockTag);
//
//                for(TagKey<Block> tag : blockVariant.secondaryBlockTags){
//                    this.getOrCreateTagBuilder(tag).addTag(blockVariant.primaryBlockTag);
//                }
//            }

            this.getOrCreateTagBuilder(JelloTags.Blocks.STICKY_BLOCKS).add(Blocks.HONEY_BLOCK);
        }
    }

    public static class ItemTagProvider extends FabricTagProvider.ItemTagProvider {

        public ItemTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
//            for(DyeableBlockVariant blockVariant : DyeableBlockVariant.getAllVariants()){
//                if(!blockVariant.createBlockItem)
//                    continue;
//
//                Set<BlockItem> blockSet = new HashSet<>();
//                for(DyeColorant dyeColorant : DyeColorantRegistry.getAllColorants()){
//                    Block block = blockVariant.getColoredBlock(dyeColorant);
//                    blockSet.add((BlockItem)block.asItem());
//                }
//
//                this.getOrCreateTagBuilder(blockVariant.primaryItemTag)
//                        .add(blockSet.toArray(new Item[]{}));
//
//                this.getOrCreateTagBuilder(blockVariant.getCommonItemTag()).addTag(blockVariant.primaryItemTag);
//
//                for(TagKey<Item> tag : blockVariant.secondaryItemTags){
//                    this.getOrCreateTagBuilder(tag).addTag(blockVariant.primaryItemTag);
//                }
//            }

//            this.getOrCreateTagBuilder(JelloTags.Items.SLIME_SLABS).add(JelloBlocks.SLIME_SLAB.asItem());
//
//            this.getOrCreateTagBuilder(JelloTags.Items.SLIME_BLOCKS).add(Blocks.SLIME_BLOCK.asItem());

            this.getOrCreateTagBuilder(JelloTags.Items.SLIME_BALLS).add(Items.SLIME_BALL);
            JelloItems.Slimeballs.SLIME_BALLS.forEach((item) -> this.getOrCreateTagBuilder(JelloTags.Items.SLIME_BALLS).add(item));

            for (Map.Entry<DyeColorant, DyeableVariantManager.DyeColorantVariantData> entry : DyeableVariantManager.getVariantMap().entrySet()) {
                //this.getOrCreateTagBuilder(JelloTags.Items.DYE).add(entry.getValue().dyeItem);

                if (DyeColorantRegistry.Constants.VANILLA_DYES.contains(entry.getKey())) {
                    this.getOrCreateTagBuilder(GelatinTags.Items.VANILLA_DYE).add(entry.getValue().dyeItem());
                }
            }
        }
    }

    //private static final Gson BIG_GSON = new GsonBuilder().setPrettyPrinting().create();

//    public static class GeneratedDyeItemTagProvider extends FabricTagProvider.ItemTagProvider {
//
//        public GeneratedDyeItemTagProvider(FabricDataGenerator dataGenerator) {
//            super(dataGenerator);
//        }
//
//        @Override
//        protected void generateTags() {
//            MessageUtil messager = new MessageUtil("JsonToRegistry");
//
//            FabricTagProvider<Item>.FabricTagBuilder<Item> tagBuilder = this.getOrCreateTagBuilder(JelloTags.Items.DYE_ITEMS);
//
//            try {
//                var colorDataBaseFile = DyeColorantRegistry.class.getClassLoader().getResourceAsStream("assets/jello/other/colorDatabase.json");
//
//                JsonArray names = JsonHelper.getArray(BIG_GSON.fromJson(new InputStreamReader(colorDataBaseFile), JsonObject.class), "colors");
//
//                for (var i = 0; i < names.size(); i++) {
//                    JsonObject currentObject = names.get(i).getAsJsonObject();
//
//                    Item dyeItem = Registry.ITEM.get(Jello.id(currentObject.get("identifierSafeName").getAsString() + "_dye"));
//
//                    if(dyeItem == Items.AIR){
//                        dyeItem = Registry.ITEM.get(Jello.id(currentObject.get("identifierSafeName").getAsString() + "_2" + "_dye"));
//                    }
//
//                    tagBuilder.add(dyeItem);
//                }
//
//                messager.stopTimerPrint("Data gen for tags based on DyeColor Database was ");
//            }catch (JsonSyntaxException | JsonIOException e) {
//                messager.failMessage("It seems that tags building has failed!");
//                e.printStackTrace();
//            }
//
//        }
//    }

    public static class DyeTagProvider extends FabricTagProvider<DyeColorant> {

        public DyeTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator, DyeColorantRegistry.DYE_COLOR);
        }

        @Override
        protected void generateTags() {
            this.getOrCreateTagBuilder(GelatinTags.DyeColor.VANILLA_DYES)
                    .add(DyeColorantRegistry.WHITE,
                            DyeColorantRegistry.ORANGE,
                            DyeColorantRegistry.MAGENTA,
                            DyeColorantRegistry.LIGHT_BLUE,
                            DyeColorantRegistry.YELLOW,
                            DyeColorantRegistry.LIME,
                            DyeColorantRegistry.PINK,
                            DyeColorantRegistry.GRAY,
                            DyeColorantRegistry.LIGHT_GRAY,
                            DyeColorantRegistry.CYAN,
                            DyeColorantRegistry.PURPLE,
                            DyeColorantRegistry.BLUE,
                            DyeColorantRegistry.BROWN,
                            DyeColorantRegistry.GREEN,
                            DyeColorantRegistry.RED,
                            DyeColorantRegistry.BLACK);
        }
    }
}
