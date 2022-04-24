package io.wispforest.jello.api.item;

import io.wispforest.jello.misc.ducks.JelloItemExtensions;
import io.wispforest.owo.util.ModCompatHelpers;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * A way to add an {@link Item} as a recipe remainder to a specific recipe
 */
public class RecipeSpecificRemainders {

    /**
     * Method that will add a recipe remainder function to an {@link Item}
     *
     * @param recipeId The Recipes Identifier
     * @param item The item to which should remained within the crafting table
     */
    public static void add(Identifier recipeId, Item item) {
        ((JelloItemExtensions) item).addRecipeSpecificRemainder(recipeId);
    }

    public static void addDeferred(Identifier recipeId, Identifier itemId) {
        ModCompatHelpers.getRegistryHelper(Registry.ITEM).runWhenPresent(itemId, item1 -> {
            ((JelloItemExtensions) item1).addRecipeSpecificRemainder(recipeId);
        });
    }

}
