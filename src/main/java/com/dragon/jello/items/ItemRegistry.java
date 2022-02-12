package com.dragon.jello.items;

import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
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
}
