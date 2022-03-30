package io.wispforest.jello.api;

import io.wispforest.jello.api.mixin.ducks.entity.JelloItemExtensions;
import io.wispforest.owo.registration.RegistryHelper;
import io.wispforest.owo.util.ModCompatHelpers;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RecipeSpecificRemainderOps {

    public static void addRemainderToItem(Identifier recipeId, Item item){
        ((JelloItemExtensions)item).addRecipeSpecificRemainder(recipeId);
    }

    public static void addRemainderToItem(Identifier recipeId, Identifier itemId) {
        ModCompatHelpers.getRegistryHelper(Registry.ITEM).runWhenPresent(itemId, item1 -> {
            ((JelloItemExtensions) item1).addRecipeSpecificRemainder(recipeId);
        });
    }

}
