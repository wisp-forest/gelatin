package io.wispforest.gelatin.dye_entries.data.recipe;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;


public class GelatinRecipeSerializers implements AutoRegistryContainer<RecipeSerializer<?>> {

    public static SpecialRecipeSerializer<DyeBlockVariantRecipe> DYE_BLOCK_VARIANT = new SpecialRecipeSerializer<>(DyeBlockVariantRecipe::new);
    public static SpecialRecipeSerializer<BedBlockVariantRecipe> BED_BLOCK_VARIANT = new SpecialRecipeSerializer<>(BedBlockVariantRecipe::new);

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
