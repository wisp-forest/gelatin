package io.wispforest.jello.misc.ducks;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface JelloItemExtensions {

    Map<Identifier, Item> getRecipeSpecificRemainder();

    boolean hasRecipeSpecificRemainder();

    boolean doseRecipeHaveRemainder(Identifier identifier);

    Item getRecipeSpecificRemainder(Identifier identifier);

    void addRecipeSpecificRemainder(Identifier recipeId);
}
