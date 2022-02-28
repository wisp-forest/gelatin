package com.dragon.jello.common.items;

import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.DyeColor;

import java.util.List;

public class ItemRegistry {

    public static class SlimeBallItemRegistry implements ItemRegistryContainer {
        public static final Item WHITE_SLIME_BALL = new MultiColorItem(DyeColor.WHITE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item ORANGE_SLIME_BALL = new MultiColorItem(DyeColor.ORANGE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item MAGENTA_SLIME_BALL = new MultiColorItem(DyeColor.MAGENTA, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item LIGHT_BLUE_SLIME_BALL = new MultiColorItem(DyeColor.LIGHT_BLUE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item YELLOW_SLIME_BALL = new MultiColorItem(DyeColor.YELLOW, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item LIME_SLIME_BALL = new MultiColorItem(DyeColor.LIME, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item PINK_SLIME_BALL = new MultiColorItem(DyeColor.PINK, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item GRAY_SLIME_BALL = new MultiColorItem(DyeColor.GRAY, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item LIGHT_GRAY_SLIME_BALL = new MultiColorItem(DyeColor.LIGHT_GRAY, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item CYAN_SLIME_BALL = new MultiColorItem(DyeColor.CYAN, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item PURPLE_SLIME_BALL = new MultiColorItem(DyeColor.PURPLE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item BLUE_SLIME_BALL = new MultiColorItem(DyeColor.BLUE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item BROWN_SLIME_BALL = new MultiColorItem(DyeColor.BROWN, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item GREEN_SLIME_BALL = new MultiColorItem(DyeColor.GREEN, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item RED_SLIME_BALL = new MultiColorItem(DyeColor.RED, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item BLACK_SLIME_BALL = new MultiColorItem(DyeColor.BLACK, (new Item.Settings()).group(ItemGroup.MISC));

        public static final List<Item> SLIME_BALLS = List
                .of(WHITE_SLIME_BALL, ORANGE_SLIME_BALL, MAGENTA_SLIME_BALL, LIGHT_BLUE_SLIME_BALL,
                        YELLOW_SLIME_BALL, LIME_SLIME_BALL, PINK_SLIME_BALL, GRAY_SLIME_BALL,
                        LIGHT_GRAY_SLIME_BALL, CYAN_SLIME_BALL, PURPLE_SLIME_BALL, BLUE_SLIME_BALL,
                        BROWN_SLIME_BALL, GREEN_SLIME_BALL, RED_SLIME_BALL, BLACK_SLIME_BALL);

    }

    public static class JelloCupItemRegistry implements ItemRegistryContainer {
        private static final FabricItemSettings JELLO_CUP_DEFAULT = new FabricItemSettings().group(ItemGroup.FOOD).fireproof();

        public static final Item SUGAR_CUP = new Item(JELLO_CUP_DEFAULT.food(JelloFoodComponents.SUGAR_CUP));

        public static final Item WHITE_JELLO_CUP = createJelloCup(DyeColor.WHITE, JelloFoodComponents.WHITE_JELLO_CUP);
        public static final Item ORANGE_JELLO_CUP = createJelloCup(DyeColor.ORANGE, JelloFoodComponents.ORANGE_JELLO_CUP);
        public static final Item MAGENTA_JELLO_CUP = createJelloCup(DyeColor.MAGENTA, JelloFoodComponents.MAGENTA_JELLO_CUP);
        public static final Item LIGHT_BLUE_JELLO_CUP = createJelloCup(DyeColor.LIGHT_BLUE, JelloFoodComponents.LIGHT_BLUE_JELLO_CUP);
        public static final Item YELLOW_JELLO_CUP = createJelloCup(DyeColor.YELLOW, JelloFoodComponents.YELLOW_JELLO_CUP);
        public static final Item LIME_JELLO_CUP = createJelloCup(DyeColor.LIME, JelloFoodComponents.LIME_JELLO_CUP);
        public static final Item PINK_JELLO_CUP = createJelloCup(DyeColor.PINK, JelloFoodComponents.PINK_JELLO_CUP);
        public static final Item GRAY_JELLO_CUP = createJelloCup(DyeColor.GRAY, JelloFoodComponents.GRAY_JELLO_CUP);
        public static final Item LIGHT_GRAY_JELLO_CUP = createJelloCup(DyeColor.LIGHT_GRAY, JelloFoodComponents.LIGHT_GRAY_JELLO_CUP);
        public static final Item CYAN_JELLO_CUP = createJelloCup(DyeColor.CYAN, JelloFoodComponents.CYAN_JELLO_CUP);
        public static final Item PURPLE_JELLO_CUP = createJelloCup(DyeColor.PURPLE, JelloFoodComponents.PURPLE_JELLO_CUP);
        public static final Item BLUE_JELLO_CUP = createJelloCup(DyeColor.BLUE, JelloFoodComponents.BLUE_JELLO_CUP);
        public static final Item BROWN_JELLO_CUP = createJelloCup(DyeColor.BROWN, JelloFoodComponents.BROWN_JELLO_CUP);
        public static final Item GREEN_JELLO_CUP = createJelloCup(DyeColor.GREEN, JelloFoodComponents.GREEN_JELLO_CUP);
        public static final Item RED_JELLO_CUP = createJelloCup(DyeColor.RED, JelloFoodComponents.RED_JELLO_CUP);
        public static final Item BLACK_JELLO_CUP = createJelloCup(DyeColor.BLACK, JelloFoodComponents.BLACK_JELLO_CUP);

        public static final List<Item> JELLO_CUP = List
                .of(WHITE_JELLO_CUP, ORANGE_JELLO_CUP, MAGENTA_JELLO_CUP, LIGHT_BLUE_JELLO_CUP,
                        YELLOW_JELLO_CUP, LIME_JELLO_CUP, PINK_JELLO_CUP, GRAY_JELLO_CUP,
                        LIGHT_GRAY_JELLO_CUP, CYAN_JELLO_CUP, PURPLE_JELLO_CUP, BLUE_JELLO_CUP,
                        BROWN_JELLO_CUP, GREEN_JELLO_CUP, RED_JELLO_CUP, BLACK_JELLO_CUP);

        private static MultiColorItem createJelloCup(DyeColor dyeColor, FoodComponent foodComponent){
            return new MultiColorItem(dyeColor, JELLO_CUP_DEFAULT.food(foodComponent), value -> value > 1);
        }
    }

    public static class MainItemRegistry implements ItemRegistryContainer{

        public static final Item SPONGE = new SpongeItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1));
    }
}
