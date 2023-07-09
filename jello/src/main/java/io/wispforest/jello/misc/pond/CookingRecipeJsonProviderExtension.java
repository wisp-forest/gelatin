package io.wispforest.jello.misc.pond;

import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;

import java.util.Optional;

public interface CookingRecipeJsonProviderExtension {

    CookingRecipeJsonBuilder.CookingRecipeJsonProvider setResultAmount(int amount);

    Optional<Integer> getResultAmount();
}
