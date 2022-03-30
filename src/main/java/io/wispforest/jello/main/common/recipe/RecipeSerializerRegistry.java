package io.wispforest.jello.main.common.recipe;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;

public class RecipeSerializerRegistry implements AutoRegistryContainer<RecipeSerializer<?>> {

    public static SpecialRecipeSerializer<ArtistPaletteRecipe> ARTIST_PALETTE = new SpecialRecipeSerializer<>(ArtistPaletteRecipe::new);

    @Override
    public Registry<RecipeSerializer<?>> getRegistry() {
        return Registry.RECIPE_SERIALIZER;
    }

    @Override
    public Class<RecipeSerializer<?>> getTargetFieldType() {
        return (Class<RecipeSerializer<?>>)(Object)RecipeSerializer.class;
    }
}
