package io.wispforest.jello.data.recipe;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class JelloRecipeSerializers implements AutoRegistryContainer<RecipeSerializer<?>> {

    public static SpecialRecipeSerializer<ArtistPaletteRecipe> ARTIST_PALETTE = new SpecialRecipeSerializer<>(ArtistPaletteRecipe::new);

    public static SpecialRecipeSerializer<GelatinSolutionRecipe> GELATIN_SOLUTION = new SpecialRecipeSerializer<>(GelatinSolutionRecipe::new);

    public static SpecialRecipeSerializer<JelloCupRecipe> JELLO_CUP = new SpecialRecipeSerializer<>(JelloCupRecipe::new);

    @Override
    public Registry<RecipeSerializer<?>> getRegistry() {
        return Registries.RECIPE_SERIALIZER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<RecipeSerializer<?>> getTargetFieldType() {
        return (Class<RecipeSerializer<?>>) (Object) RecipeSerializer.class;
    }
}
