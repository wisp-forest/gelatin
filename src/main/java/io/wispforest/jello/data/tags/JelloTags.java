package io.wispforest.jello.data.tags;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.Jello;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class JelloTags {
    private static final String COMMON_NAMESPACE = "c";

    public static class Blocks {

        //Common

        public static final TagKey<Block> STAINED_GLASS_PANES = registerCommon("stained_glass_pane");
        public static final TagKey<Block> STAINED_GLASS = registerCommon( "stained_glass");

        public static final TagKey<Block> CONCRETE = registerCommon("concrete");
        public static final TagKey<Block> CONCRETE_POWDER = registerCommon( "concrete_powder");

        public static final TagKey<Block> C_SLIME_SLABS = registerCommon("slime_slabs");
        public static final TagKey<Block> C_SLIME_BLOCKS = registerCommon("slime_blocks");

        public static final TagKey<Block> C_STICKY_BLOCKS = registerCommon("sticky_blocks");

        //Jello

        public static final TagKey<Block> SLIME_SLABS = registerJello("slime_slabs");
        public static final TagKey<Block> COLORED_SLIME_SLABS = registerJello("colored_slime_slabs");

        public static final TagKey<Block> SLIME_BLOCKS = registerJello("slime_blocks");
        public static final TagKey<Block> COLORED_SLIME_BLOCKS = registerJello("colored_slime_blocks");

        public static final TagKey<Block> STICKY_BLOCKS = registerCommon("sticky_blocks");

        private static TagKey<Block> registerCommon(String path) {
            return register(common(path));
        }

        private static TagKey<Block> registerJello(String path) {
            return register(jello(path));
        }

        private static TagKey<Block> register(Identifier id) {
            return TagKey.of(Registry.BLOCK_KEY, id);
        }
    }

    public static class Items {

        //Common

        public static final TagKey<Item> C_SLIME_BLOCKS = registerCommon("slime_blocks");

        public static final TagKey<Item> C_SLIME_SLABS = registerCommon("slime_slabs");

        public static final TagKey<Item> C_SLIME_BALLS = registerCommon( "slime_balls");

        public static final TagKey<Item> C_DYE = registerCommon("dyes");

        public static final TagKey<Item> C_WOOL = registerCommon("wool");

        //Jello

        public static final TagKey<Item> ALL_COLORED_VARIANTS = registerJello("all_colored_variants");

        public static final TagKey<Item> SLIME_BLOCKS = registerJello("slime_blocks");

        public static final TagKey<Item> SLIME_SLABS = registerJello("slime_slabs");

        public static final TagKey<Item> SLIME_BALLS = registerJello( "slime_balls");

        public static final TagKey<Item> DYE = registerJello("dyes");

        public static final TagKey<Item> VANILLA_DYE = registerJello("vanilla_dye_items");

        private static TagKey<Item> registerCommon(String path) {
            return register(common(path));
        }

        private static TagKey<Item> registerJello(String path) {
            return register(jello(path));
        }

        private static TagKey<Item> register(Identifier id) {
            return TagKey.of(Registry.ITEM_KEY, id);
        }
    }

    public static class DyeColor {
        public static final TagKey<DyeColorant> VANILLA_DYES = register(new Identifier(COMMON_NAMESPACE, "vanilla_dye"));

        private static TagKey<DyeColorant> registerCommon(String path) {
            return register(common(path));
        }

        private static TagKey<DyeColorant> registerJello(String path) {
            return register(jello(path));
        }

        private static TagKey<DyeColorant> register(Identifier id) {
            return TagKey.of(DyeColorantRegistry.DYE_COLOR_KEY, id);
        }
    }

    private static Identifier common(String path){
        return new Identifier(COMMON_NAMESPACE, path);
    }

    private static Identifier jello(String path){
        return Jello.id(path);
    }
}
