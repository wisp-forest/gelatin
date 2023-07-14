package io.wispforest.gelatin.dye_registry.data;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class GelatinTags {

    private static final String COMMON_NAMESPACE = "c";

    public static class Items {

        public static final TagKey<Item> ALL_COLORED_VARIANTS = registerJello("all_colored_variants");

        public static final TagKey<Item> DYE = registerJello("dyes");

        public static final TagKey<Item> C_DYE = registerCommon("dyes");

        public static final TagKey<Item> C_WOOL = registerCommon("wool");

        public static final TagKey<Item> VANILLA_DYE = registerJello("vanilla_dye_items");

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
        return GelatinConstants.id(path);
    }
}
