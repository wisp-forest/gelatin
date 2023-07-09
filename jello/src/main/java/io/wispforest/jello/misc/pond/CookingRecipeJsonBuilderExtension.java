package io.wispforest.jello.misc.pond;

import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;

import java.util.Optional;

public interface CookingRecipeJsonBuilderExtension {

    CookingRecipeJsonBuilder setResultAmount(int amount);

    Optional<Integer> getResultAmount();
}
