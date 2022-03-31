package io.wispforest.jello.data.providers;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.data.tags.JelloTags;
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
            this.getOrCreateTagBuilder(JelloTags.Blocks.CONCRETE)
                    .add(Blocks.WHITE_CONCRETE,
                            Blocks.ORANGE_CONCRETE,
                            Blocks.MAGENTA_CONCRETE,
                            Blocks.LIGHT_BLUE_CONCRETE,
                            Blocks.YELLOW_CONCRETE,
                            Blocks.LIME_CONCRETE,
                            Blocks.PINK_CONCRETE,
                            Blocks.GRAY_CONCRETE,
                            Blocks.LIGHT_GRAY_CONCRETE,
                            Blocks.CYAN_CONCRETE,
                            Blocks.PURPLE_CONCRETE,
                            Blocks.BLUE_CONCRETE,
                            Blocks.BROWN_CONCRETE,
                            Blocks.GREEN_CONCRETE,
                            Blocks.RED_CONCRETE,
                            Blocks.BLACK_CONCRETE);

            this.getOrCreateTagBuilder(JelloTags.Blocks.CONCRETE_POWDER)
                    .add(Blocks.WHITE_CONCRETE_POWDER,
                            Blocks.ORANGE_CONCRETE_POWDER,
                            Blocks.MAGENTA_CONCRETE_POWDER,
                            Blocks.LIGHT_BLUE_CONCRETE_POWDER,
                            Blocks.YELLOW_CONCRETE_POWDER,
                            Blocks.LIME_CONCRETE_POWDER,
                            Blocks.PINK_CONCRETE_POWDER,
                            Blocks.GRAY_CONCRETE_POWDER,
                            Blocks.LIGHT_GRAY_CONCRETE_POWDER,
                            Blocks.CYAN_CONCRETE_POWDER,
                            Blocks.PURPLE_CONCRETE_POWDER,
                            Blocks.BLUE_CONCRETE_POWDER,
                            Blocks.BROWN_CONCRETE_POWDER,
                            Blocks.GREEN_CONCRETE_POWDER,
                            Blocks.RED_CONCRETE_POWDER,
                            Blocks.BLACK_CONCRETE_POWDER
                    );

            this.getOrCreateTagBuilder(JelloTags.Blocks.GLASS_PANES)
                    .add(Blocks.GLASS_PANE,
                            Blocks.WHITE_STAINED_GLASS_PANE,
                            Blocks.ORANGE_STAINED_GLASS_PANE,
                            Blocks.MAGENTA_STAINED_GLASS_PANE,
                            Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
                            Blocks.YELLOW_STAINED_GLASS_PANE,
                            Blocks.LIME_STAINED_GLASS_PANE,
                            Blocks.PINK_STAINED_GLASS_PANE,
                            Blocks.GRAY_STAINED_GLASS_PANE,
                            Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
                            Blocks.CYAN_STAINED_GLASS_PANE,
                            Blocks.PURPLE_STAINED_GLASS_PANE,
                            Blocks.BLUE_STAINED_GLASS_PANE,
                            Blocks.BROWN_STAINED_GLASS_PANE,
                            Blocks.GREEN_STAINED_GLASS_PANE,
                            Blocks.RED_STAINED_GLASS_PANE,
                            Blocks.BLACK_STAINED_GLASS_PANE);

            this.getOrCreateTagBuilder(JelloTags.Blocks.STAINED_GLASS)
                    .add(Blocks.GLASS,
                            Blocks.WHITE_STAINED_GLASS,
                            Blocks.ORANGE_STAINED_GLASS,
                            Blocks.MAGENTA_STAINED_GLASS,
                            Blocks.LIGHT_BLUE_STAINED_GLASS,
                            Blocks.YELLOW_STAINED_GLASS,
                            Blocks.LIME_STAINED_GLASS,
                            Blocks.PINK_STAINED_GLASS,
                            Blocks.GRAY_STAINED_GLASS,
                            Blocks.LIGHT_GRAY_STAINED_GLASS,
                            Blocks.CYAN_STAINED_GLASS,
                            Blocks.PURPLE_STAINED_GLASS,
                            Blocks.BLUE_STAINED_GLASS,
                            Blocks.BROWN_STAINED_GLASS,
                            Blocks.GREEN_STAINED_GLASS,
                            Blocks.RED_STAINED_GLASS,
                            Blocks.BLACK_STAINED_GLASS);

            this.getOrCreateTagBuilder(JelloTags.Blocks.COLORED_SLIME_SLABS);//BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach(this.getOrCreateTagBuilder(JelloTags.Blocks.COLORED_SLIME_SLABS)::add);

            this.getOrCreateTagBuilder(JelloTags.Blocks.COLORED_SLIME_BLOCKS);//BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach(this.getOrCreateTagBuilder(JelloTags.Blocks.COLORED_SLIME_BLOCKS)::add);

            this.getOrCreateTagBuilder(JelloTags.Blocks.SLIME_SLABS).addTag(JelloTags.Blocks.COLORED_SLIME_SLABS).add(JelloBlocks.SLIME_SLAB);

            this.getOrCreateTagBuilder(JelloTags.Blocks.SLIME_BLOCKS).addTag(JelloTags.Blocks.COLORED_SLIME_BLOCKS).add(Blocks.SLIME_BLOCK);

            this.getOrCreateTagBuilder(JelloTags.Blocks.STICKY_BLOCKS)
                    .addTag(JelloTags.Blocks.SLIME_BLOCKS).addTag(JelloTags.Blocks.SLIME_SLABS)
                    .add(Blocks.HONEY_BLOCK);
        }
    }

    public static class ItemTagProvider extends FabricTagProvider.ItemTagProvider {

        public ItemTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
            this.getOrCreateTagBuilder(JelloTags.Items.SLIME_SLABS).add(JelloBlocks.SLIME_SLAB.asItem());
//            BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block) -> this.getOrCreateTagBuilder(JelloTags.Items.SLIME_SLABS).add(block.asItem()));

            this.getOrCreateTagBuilder(JelloTags.Items.SLIME_BLOCKS).add(Blocks.SLIME_BLOCK.asItem());
            //BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block) -> this.getOrCreateTagBuilder(JelloTags.Items.SLIME_BLOCKS).add(block.asItem()));

            this.getOrCreateTagBuilder(JelloTags.Items.SLIME_BALLS).add(Items.SLIME_BALL);
            JelloItems.Slimeballs.SLIME_BALLS.forEach((item) -> this.getOrCreateTagBuilder(JelloTags.Items.SLIME_BALLS).add(item));

            for (Map.Entry<DyeColorant, DyedVariantContainer> entry : DyedVariantContainer.getVariantMap().entrySet()) {
                this.getOrCreateTagBuilder(JelloTags.Items.DYE_ITEMS).add(entry.getValue().dyeItem);

                if (DyeColorantRegistry.Constants.VANILLA_DYES.contains(entry.getKey())) {
                    this.getOrCreateTagBuilder(JelloTags.Items.VANILLA_DYE_ITEMS).add(entry.getValue().dyeItem);
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
            super(dataGenerator, DyeColorantRegistry.DYE_COLOR, "Dye Tags");
        }

        @Override
        protected void generateTags() {
            this.getOrCreateTagBuilder(JelloTags.DyeColor.VANILLA_DYES)
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
