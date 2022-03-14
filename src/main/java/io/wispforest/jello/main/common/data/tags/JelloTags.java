package io.wispforest.jello.main.common.data.tags;

import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.dye.DyeColorant;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class JelloTags {
    private static final String COMMON_NAMESPACE = "c";

    public static class Blocks{

        public static final TagKey<Block> GLASS_PANES = register(new Identifier(COMMON_NAMESPACE, "glass_panes"));
        public static final TagKey<Block> STAINED_GLASS = register(new Identifier(COMMON_NAMESPACE, "stained_glass"));

        public static final TagKey<Block> CONCRETE = register(new Identifier(COMMON_NAMESPACE, "concrete"));
        public static final TagKey<Block> CONCRETE_POWDER = register(new Identifier(COMMON_NAMESPACE, "concrete_powder"));

        public static final TagKey<Block> COLORED_SLIME_SLABS = register(new Identifier(Jello.MODID, "colored_slime_slabs"));
        public static final TagKey<Block> SLIME_SLABS = register(new Identifier(Jello.MODID, "slime_slabs"));

        public static final TagKey<Block> COLORED_SLIME_BLOCKS = register(new Identifier(Jello.MODID, "colored_slime_blocks"));
        public static final TagKey<Block> SLIME_BLOCKS = register(new Identifier(COMMON_NAMESPACE, "slime_blocks"));

        public static final TagKey<Block> STICKY_BLOCKS = register(new Identifier(COMMON_NAMESPACE, "sticky_blocks"));

        private static TagKey<Block> register(Identifier id) {
            return TagKey.of(Registry.BLOCK_KEY, id);
        }
    }

    public static class Items{
        public static final TagKey<Item> SLIME_BLOCKS = register(new Identifier(COMMON_NAMESPACE, "slime_blocks"));

        public static final TagKey<Item> SLIME_SLABS = register(new Identifier(COMMON_NAMESPACE, "slime_slabs"));

        public static final TagKey<Item> SLIME_BALLS = register(new Identifier(COMMON_NAMESPACE, "slime_balls"));

        public static final TagKey<Item> DYE_ITEMS = register(new Identifier(Jello.MODID, "dyes"));

        private static TagKey<Item> register(Identifier id) {
            return TagKey.of(Registry.ITEM_KEY, id);
        }
    }

    public static class DyeColor{
        public static final TagKey<DyeColorant> VANILLA_DYES = register(new Identifier(COMMON_NAMESPACE, "vanilla_dye"));

        private static TagKey<DyeColorant> register(Identifier id) {
            return TagKey.of(DyeColorRegistry.DYE_COLOR_KEY, id);
        }
    }
}
