package io.wispforest.gelatin.dye_entries.data.recipe;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;


public class GelatinRecipeSerializers {

    public static SpecialRecipeSerializer<DyeBlockVariantRecipe> DYE_BLOCK_VARIANT = new SpecialRecipeSerializer<>(DyeBlockVariantRecipe::new);
    public static SpecialRecipeSerializer<BedBlockVariantRecipe> BED_BLOCK_VARIANT = new SpecialRecipeSerializer<>(BedBlockVariantRecipe::new);

    public static void init(){
        Registry.register(Registry.RECIPE_SERIALIZER, GelatinConstants.id("dye_block_variant"), DYE_BLOCK_VARIANT);
        Registry.register(Registry.RECIPE_SERIALIZER, GelatinConstants.id("bed_block_variant"), BED_BLOCK_VARIANT);
    }
}
