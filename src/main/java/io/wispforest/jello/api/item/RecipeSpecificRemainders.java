package io.wispforest.jello.api.item;

import io.wispforest.jello.misc.ducks.JelloItemExtensions;
import io.wispforest.owo.util.ModCompatHelpers;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RecipeSpecificRemainders {

    public static void add(Identifier recipeId, Item item) {
        ((JelloItemExtensions) item).addRecipeSpecificRemainder(recipeId);
    }

    public static void addDeferred(Identifier recipeId, Identifier itemId) {
        ModCompatHelpers.getRegistryHelper(Registry.ITEM).runWhenPresent(itemId, item1 -> {
            ((JelloItemExtensions) item1).addRecipeSpecificRemainder(recipeId);
        });
    }

}
