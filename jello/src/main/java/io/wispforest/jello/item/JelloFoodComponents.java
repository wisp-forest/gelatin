package io.wispforest.jello.item;

import net.minecraft.item.FoodComponent;

public class JelloFoodComponents {
    public static final FoodComponent BOWL_OF_SUGAR = new FoodComponent.Builder().alwaysEdible().snack().hunger(1).saturationModifier(-1.5F).build();
    public static final FoodComponent SUGAR_CUP = new FoodComponent.Builder().alwaysEdible().snack().hunger(1).saturationModifier(0F).build();
}
