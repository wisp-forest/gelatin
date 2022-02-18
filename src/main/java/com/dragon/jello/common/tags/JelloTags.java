package com.dragon.jello.common.tags;

import com.dragon.jello.common.Jello;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class JelloTags {
    private static final String COMMON_NAMESPACE = "c";

    public static class Blocks{

        public static final Tag.Identified<Block> GLASS_PANES = TagFactory.BLOCK.create(new Identifier(COMMON_NAMESPACE, "glass_panes"));

        public static final Tag.Identified<Block> CONCRETE = TagFactory.BLOCK.create(new Identifier(COMMON_NAMESPACE, "concrete"));
        public static final Tag.Identified<Block> CONCRETE_POWDER = TagFactory.BLOCK.create(new Identifier(COMMON_NAMESPACE, "concrete_powder"));

        public static final Tag.Identified<Block> COLORED_SLIME_SLABS = TagFactory.BLOCK.create(new Identifier(Jello.MODID, "colored_slime_slabs"));
        public static final Tag.Identified<Block> SLIME_SLABS = TagFactory.BLOCK.create(new Identifier(Jello.MODID, "slime_slabs"));

        public static final Tag.Identified<Block> COLORED_SLIME_BLOCKS = TagFactory.BLOCK.create(new Identifier(Jello.MODID, "colored_slime_blocks"));
        public static final Tag.Identified<Block> SLIME_BLOCKS = TagFactory.BLOCK.create(new Identifier(COMMON_NAMESPACE, "slime_blocks"));

        public static final Tag.Identified<Block> STICKY_BLOCKS = TagFactory.BLOCK.create(new Identifier(COMMON_NAMESPACE, "sticky_blocks"));

    }

    public static class Items{
        public static final Tag.Identified<Item> SLIME_BLOCKS = TagFactory.ITEM.create(new Identifier(COMMON_NAMESPACE, "slime_blocks"));

        public static final Tag.Identified<Item> SLIME_SLABS = TagFactory.ITEM.create(new Identifier(COMMON_NAMESPACE, "slime_slabs"));

        public static final Tag.Identified<Item> SLIME_BALLS = TagFactory.ITEM.create(new Identifier(COMMON_NAMESPACE, "slime_balls"));
    }

}
