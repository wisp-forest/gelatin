package io.wispforest.jello.data.recipe;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;

public class JelloRecipeSerializers implements AutoRegistryContainer<RecipeSerializer<?>> {

    public static SpecialRecipeSerializer<ArtistPaletteRecipe> ARTIST_PALETTE = new SpecialRecipeSerializer<>(ArtistPaletteRecipe::new);

    @Override
    public Registry<RecipeSerializer<?>> getRegistry() {
        return Registry.RECIPE_SERIALIZER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<RecipeSerializer<?>> getTargetFieldType() {
        return (Class<RecipeSerializer<?>>) (Object) RecipeSerializer.class;
    }
}
