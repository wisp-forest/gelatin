package io.wispforest.jello.main.common.items;

import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.item.DyenamicDyeItem;
import io.wispforest.jello.main.common.items.dyebundle.DyeBundle;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.List;

public class ItemRegistry {

    public static class SlimeBallItemRegistry implements ItemRegistryContainer {
        public static final Item WHITE_SLIME_BALL = new MultiColorItem(DyeColorRegistry.WHITE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item ORANGE_SLIME_BALL = new MultiColorItem(DyeColorRegistry.ORANGE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item MAGENTA_SLIME_BALL = new MultiColorItem(DyeColorRegistry.MAGENTA, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item LIGHT_BLUE_SLIME_BALL = new MultiColorItem(DyeColorRegistry.LIGHT_BLUE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item YELLOW_SLIME_BALL = new MultiColorItem(DyeColorRegistry.YELLOW, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item LIME_SLIME_BALL = new MultiColorItem(DyeColorRegistry.LIME, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item PINK_SLIME_BALL = new MultiColorItem(DyeColorRegistry.PINK, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item GRAY_SLIME_BALL = new MultiColorItem(DyeColorRegistry.GRAY, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item LIGHT_GRAY_SLIME_BALL = new MultiColorItem(DyeColorRegistry.LIGHT_GRAY, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item CYAN_SLIME_BALL = new MultiColorItem(DyeColorRegistry.CYAN, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item PURPLE_SLIME_BALL = new MultiColorItem(DyeColorRegistry.PURPLE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item BLUE_SLIME_BALL = new MultiColorItem(DyeColorRegistry.BLUE, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item BROWN_SLIME_BALL = new MultiColorItem(DyeColorRegistry.BROWN, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item GREEN_SLIME_BALL = new MultiColorItem(DyeColorRegistry.GREEN, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item RED_SLIME_BALL = new MultiColorItem(DyeColorRegistry.RED, (new Item.Settings()).group(ItemGroup.MISC));
        public static final Item BLACK_SLIME_BALL = new MultiColorItem(DyeColorRegistry.BLACK, (new Item.Settings()).group(ItemGroup.MISC));

        public static final List<Item> SLIME_BALLS = List
                .of(WHITE_SLIME_BALL, ORANGE_SLIME_BALL, MAGENTA_SLIME_BALL, LIGHT_BLUE_SLIME_BALL,
                        YELLOW_SLIME_BALL, LIME_SLIME_BALL, PINK_SLIME_BALL, GRAY_SLIME_BALL,
                        LIGHT_GRAY_SLIME_BALL, CYAN_SLIME_BALL, PURPLE_SLIME_BALL, BLUE_SLIME_BALL,
                        BROWN_SLIME_BALL, GREEN_SLIME_BALL, RED_SLIME_BALL, BLACK_SLIME_BALL);

    }

    public static class JelloCupItemRegistry implements ItemRegistryContainer {
        private static final FabricItemSettings JELLO_CUP_DEFAULT = new FabricItemSettings().group(ItemGroup.FOOD).fireproof();

        public static final Item SUGAR_CUP = new Item(JELLO_CUP_DEFAULT.food(JelloFoodComponents.SUGAR_CUP));

        public static final Item WHITE_JELLO_CUP = createJelloCup(DyeColorRegistry.WHITE, JelloFoodComponents.WHITE_JELLO_CUP);
        public static final Item ORANGE_JELLO_CUP = createJelloCup(DyeColorRegistry.ORANGE, JelloFoodComponents.ORANGE_JELLO_CUP);
        public static final Item MAGENTA_JELLO_CUP = createJelloCup(DyeColorRegistry.MAGENTA, JelloFoodComponents.MAGENTA_JELLO_CUP);
        public static final Item LIGHT_BLUE_JELLO_CUP = createJelloCup(DyeColorRegistry.LIGHT_BLUE, JelloFoodComponents.LIGHT_BLUE_JELLO_CUP);
        public static final Item YELLOW_JELLO_CUP = createJelloCup(DyeColorRegistry.YELLOW, JelloFoodComponents.YELLOW_JELLO_CUP);
        public static final Item LIME_JELLO_CUP = createJelloCup(DyeColorRegistry.LIME, JelloFoodComponents.LIME_JELLO_CUP);
        public static final Item PINK_JELLO_CUP = createJelloCup(DyeColorRegistry.PINK, JelloFoodComponents.PINK_JELLO_CUP);
        public static final Item GRAY_JELLO_CUP = createJelloCup(DyeColorRegistry.GRAY, JelloFoodComponents.GRAY_JELLO_CUP);
        public static final Item LIGHT_GRAY_JELLO_CUP = createJelloCup(DyeColorRegistry.LIGHT_GRAY, JelloFoodComponents.LIGHT_GRAY_JELLO_CUP);
        public static final Item CYAN_JELLO_CUP = createJelloCup(DyeColorRegistry.CYAN, JelloFoodComponents.CYAN_JELLO_CUP);
        public static final Item PURPLE_JELLO_CUP = createJelloCup(DyeColorRegistry.PURPLE, JelloFoodComponents.PURPLE_JELLO_CUP);
        public static final Item BLUE_JELLO_CUP = createJelloCup(DyeColorRegistry.BLUE, JelloFoodComponents.BLUE_JELLO_CUP);
        public static final Item BROWN_JELLO_CUP = createJelloCup(DyeColorRegistry.BROWN, JelloFoodComponents.BROWN_JELLO_CUP);
        public static final Item GREEN_JELLO_CUP = createJelloCup(DyeColorRegistry.GREEN, JelloFoodComponents.GREEN_JELLO_CUP);
        public static final Item RED_JELLO_CUP = createJelloCup(DyeColorRegistry.RED, JelloFoodComponents.RED_JELLO_CUP);
        public static final Item BLACK_JELLO_CUP = createJelloCup(DyeColorRegistry.BLACK, JelloFoodComponents.BLACK_JELLO_CUP);

        public static final List<Item> JELLO_CUP = List
                .of(WHITE_JELLO_CUP, ORANGE_JELLO_CUP, MAGENTA_JELLO_CUP, LIGHT_BLUE_JELLO_CUP,
                        YELLOW_JELLO_CUP, LIME_JELLO_CUP, PINK_JELLO_CUP, GRAY_JELLO_CUP,
                        LIGHT_GRAY_JELLO_CUP, CYAN_JELLO_CUP, PURPLE_JELLO_CUP, BLUE_JELLO_CUP,
                        BROWN_JELLO_CUP, GREEN_JELLO_CUP, RED_JELLO_CUP, BLACK_JELLO_CUP);

        private static MultiColorItem createJelloCup(DyeColorant dyeColor, FoodComponent foodComponent){
            return new MultiColorItem(dyeColor, JELLO_CUP_DEFAULT.food(foodComponent), value -> value > 1);
        }
    }

    public static class MainItemRegistry implements ItemRegistryContainer{

        public static final Item SPONGE = new SpongeItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1));
        public static final Item DYNAMIC_DYE = new DyenamicDyeItem(new FabricItemSettings().group(ItemGroup.TOOLS));
        public static final Item DYE_BUNDLE = new DyeBundle(new FabricItemSettings().group(ItemGroup.TOOLS));
    }
}
