package io.wispforest.jello.item;

import io.wispforest.common.util.BetterItemOps;
import io.wispforest.dye_entries.item.ColoredItem;
import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;
import io.wispforest.jello.item.dyebundle.DyeBundleItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.List;

public class JelloItems implements ItemRegistryContainer {

    public static final Item SPONGE = new SpongeItem(new OwoItemSettings().group(ItemGroup.TOOLS).tab(0).maxCount(1));
    public static final Item DYE_BUNDLE = new DyeBundleItem(new OwoItemSettings().group(ItemGroup.TOOLS).tab(0));
    public static final Item EMPTY_ARTIST_PALETTE = new Item(new OwoItemSettings().group(ItemGroup.MISC).tab(0).maxCount(1));
    public static final Item ARTIST_PALETTE = new ArtistPaletteItem(new OwoItemSettings().group(ItemGroup.MISC).tab(0).maxCount(1));

    public static class Slimeballs implements ItemRegistryContainer {
        public static final Item WHITE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.WHITE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item ORANGE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.ORANGE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item MAGENTA_SLIME_BALL = new ColoredItem(DyeColorantRegistry.MAGENTA, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item LIGHT_BLUE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.LIGHT_BLUE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item YELLOW_SLIME_BALL = new ColoredItem(DyeColorantRegistry.YELLOW, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item LIME_SLIME_BALL = new ColoredItem(DyeColorantRegistry.LIME, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item PINK_SLIME_BALL = new ColoredItem(DyeColorantRegistry.PINK, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item GRAY_SLIME_BALL = new ColoredItem(DyeColorantRegistry.GRAY, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item LIGHT_GRAY_SLIME_BALL = new ColoredItem(DyeColorantRegistry.LIGHT_GRAY, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item CYAN_SLIME_BALL = new ColoredItem(DyeColorantRegistry.CYAN, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item PURPLE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.PURPLE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item BLUE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.BLUE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item BROWN_SLIME_BALL = new ColoredItem(DyeColorantRegistry.BROWN, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item GREEN_SLIME_BALL = new ColoredItem(DyeColorantRegistry.GREEN, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item RED_SLIME_BALL = new ColoredItem(DyeColorantRegistry.RED, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item BLACK_SLIME_BALL = new ColoredItem(DyeColorantRegistry.BLACK, (new Item.Settings()).group(ItemGroup.MISC));

        public static final List<Item> SLIME_BALLS = List
                .of(WHITE_SLIME_BALL, ORANGE_SLIME_BALL, MAGENTA_SLIME_BALL, LIGHT_BLUE_SLIME_BALL,
                        YELLOW_SLIME_BALL, LIME_SLIME_BALL, PINK_SLIME_BALL, GRAY_SLIME_BALL,
                        LIGHT_GRAY_SLIME_BALL, CYAN_SLIME_BALL, PURPLE_SLIME_BALL, BLUE_SLIME_BALL,
                        BROWN_SLIME_BALL, GREEN_SLIME_BALL, RED_SLIME_BALL, BLACK_SLIME_BALL);
    }

    public static class JelloCups implements ItemRegistryContainer {
        private static final OwoItemSettings JELLO_CUP_DEFAULT = new OwoItemSettings().group(ItemGroup.FOOD).tab(0).fireproof();

        public static final Item SUGAR_CUP = new Item(BetterItemOps.copyFrom(JELLO_CUP_DEFAULT).food(JelloFoodComponents.SUGAR_CUP));

        public static final Item WHITE_JELLO_CUP = createJelloCup(DyeColorantRegistry.WHITE, JelloFoodComponents.WHITE_JELLO_CUP);
        public static final Item ORANGE_JELLO_CUP = createJelloCup(DyeColorantRegistry.ORANGE, JelloFoodComponents.ORANGE_JELLO_CUP);
        public static final Item MAGENTA_JELLO_CUP = createJelloCup(DyeColorantRegistry.MAGENTA, JelloFoodComponents.MAGENTA_JELLO_CUP);
        public static final Item LIGHT_BLUE_JELLO_CUP = createJelloCup(DyeColorantRegistry.LIGHT_BLUE, JelloFoodComponents.LIGHT_BLUE_JELLO_CUP);
        public static final Item YELLOW_JELLO_CUP = createJelloCup(DyeColorantRegistry.YELLOW, JelloFoodComponents.YELLOW_JELLO_CUP);
        public static final Item LIME_JELLO_CUP = createJelloCup(DyeColorantRegistry.LIME, JelloFoodComponents.LIME_JELLO_CUP);
        public static final Item PINK_JELLO_CUP = createJelloCup(DyeColorantRegistry.PINK, JelloFoodComponents.PINK_JELLO_CUP);
        public static final Item GRAY_JELLO_CUP = createJelloCup(DyeColorantRegistry.GRAY, JelloFoodComponents.GRAY_JELLO_CUP);
        public static final Item LIGHT_GRAY_JELLO_CUP = createJelloCup(DyeColorantRegistry.LIGHT_GRAY, JelloFoodComponents.LIGHT_GRAY_JELLO_CUP);
        public static final Item CYAN_JELLO_CUP = createJelloCup(DyeColorantRegistry.CYAN, JelloFoodComponents.CYAN_JELLO_CUP);
        public static final Item PURPLE_JELLO_CUP = createJelloCup(DyeColorantRegistry.PURPLE, JelloFoodComponents.PURPLE_JELLO_CUP);
        public static final Item BLUE_JELLO_CUP = createJelloCup(DyeColorantRegistry.BLUE, JelloFoodComponents.BLUE_JELLO_CUP);
        public static final Item BROWN_JELLO_CUP = createJelloCup(DyeColorantRegistry.BROWN, JelloFoodComponents.BROWN_JELLO_CUP);
        public static final Item GREEN_JELLO_CUP = createJelloCup(DyeColorantRegistry.GREEN, JelloFoodComponents.GREEN_JELLO_CUP);
        public static final Item RED_JELLO_CUP = createJelloCup(DyeColorantRegistry.RED, JelloFoodComponents.RED_JELLO_CUP);
        public static final Item BLACK_JELLO_CUP = createJelloCup(DyeColorantRegistry.BLACK, JelloFoodComponents.BLACK_JELLO_CUP);

        public static final List<Item> JELLO_CUP = List
                .of(WHITE_JELLO_CUP, ORANGE_JELLO_CUP, MAGENTA_JELLO_CUP, LIGHT_BLUE_JELLO_CUP,
                        YELLOW_JELLO_CUP, LIME_JELLO_CUP, PINK_JELLO_CUP, GRAY_JELLO_CUP,
                        LIGHT_GRAY_JELLO_CUP, CYAN_JELLO_CUP, PURPLE_JELLO_CUP, BLUE_JELLO_CUP,
                        BROWN_JELLO_CUP, GREEN_JELLO_CUP, RED_JELLO_CUP, BLACK_JELLO_CUP);

        private static ColoredItem createJelloCup(DyeColorant dyeColor, FoodComponent foodComponent) {
            return new ColoredItem(dyeColor, BetterItemOps.copyFrom(JELLO_CUP_DEFAULT).food(foodComponent), value -> value > 1);
        }
    }
}

