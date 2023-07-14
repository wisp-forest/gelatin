package io.wispforest.jello.data;

import io.wispforest.jello.Jello;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;


public class JelloTags {
    private static final String COMMON_NAMESPACE = "c";

    public static class Blocks {

        public static final TagKey<Block> SLIME_SLABS = registerJello("slime_slabs");
        public static final TagKey<Block> C_SLIME_SLABS = registerCommon("slime_slabs");
        public static final TagKey<Block> COLORED_SLIME_SLABS = registerJello("colored_slime_slabs");

        public static final TagKey<Block> SLIME_BLOCKS = registerJello("slime_blocks");
        public static final TagKey<Block> C_SLIME_BLOCKS = registerCommon("slime_blocks");
        public static final TagKey<Block> COLORED_SLIME_BLOCKS = registerJello("colored_slime_blocks");

        public static final TagKey<Block> STICKY_BLOCKS = registerCommon("sticky_blocks");

        private static TagKey<Block> registerCommon(String path) {
            return register(common(path));
        }

        private static TagKey<Block> registerJello(String path) {
            return register(jello(path));
        }

        private static TagKey<Block> register(Identifier id) {
            return TagKey.of(RegistryKeys.BLOCK, id);
        }
    }

    public static class Items {

        public static final TagKey<Item> SLIME_BLOCKS = registerJello("slime_blocks");
        public static final TagKey<Item> C_SLIME_BLOCKS = registerCommon("slime_blocks");

        public static final TagKey<Item> SLIME_SLABS = registerJello("slime_slabs");
        public static final TagKey<Item> C_SLIME_SLABS = registerCommon("slime_slabs");

        public static final TagKey<Item> SLIME_BALLS = registerJello( "slime_balls");
        public static final TagKey<Item> C_SLIME_BALLS = registerCommon( "slime_balls");

        private static TagKey<Item> registerCommon(String path) {
            return register(common(path));
        }

        private static TagKey<Item> registerJello(String path) {
            return register(jello(path));
        }

        private static TagKey<Item> register(Identifier id) {
            return TagKey.of(RegistryKeys.ITEM, id);
        }
    }

    private static Identifier common(String path){
        return new Identifier(COMMON_NAMESPACE, path);
    }

    private static Identifier jello(String path){
        return Jello.id(path);
    }
}
