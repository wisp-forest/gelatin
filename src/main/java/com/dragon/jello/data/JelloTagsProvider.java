package com.dragon.jello.data;

import com.dragon.jello.blocks.BlockRegistry;
import com.dragon.jello.tags.JelloBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.stream.Stream;

public class JelloTagsProvider {

    public static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {

        public BlockTagProvider(FabricDataGenerator dataGenerator) {
            super(dataGenerator);
        }

        @Override
        protected void generateTags() {
            this.getOrCreateTagBuilder(JelloBlockTags.CONCRETE)
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

            this.getOrCreateTagBuilder(JelloBlockTags.COLORED_GLASS_PANES)
                    .add(Blocks.WHITE_STAINED_GLASS_PANE,
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

            BlockRegistry.SlimeBlockRegistry.SLIME_BLOCKS.forEach(this.getOrCreateTagBuilder(JelloBlockTags.COLORED_SLIME_BLOCKS)::add);
        }
    }

}
