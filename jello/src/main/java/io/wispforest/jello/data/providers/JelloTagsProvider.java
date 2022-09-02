package io.wispforest.jello.data.providers;

import io.wispforest.jello.data.JelloTags;
import io.wispforest.jello.item.JelloItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

public class JelloTagsProvider {

//    public static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
//
//        public BlockTagProvider(FabricDataGenerator dataGenerator) {
//            super(dataGenerator);
//        }
//
//        @Override
//        protected void generateTags() {
//            this.getOrCreateTagBuilder(JelloTags.Blocks.STICKY_BLOCKS).add(Blocks.HONEY_BLOCK);
//        }
//    }

    public static class ItemTagProvider extends FabricTagProvider.ItemTagProvider {

        public ItemTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
            this.getOrCreateTagBuilder(JelloTags.Items.SLIME_BALLS).add(Items.SLIME_BALL);
            JelloItems.Slimeballs.SLIME_BALLS.forEach((item) -> this.getOrCreateTagBuilder(JelloTags.Items.SLIME_BALLS).add(item));
        }
    }
}
