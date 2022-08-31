package io.wispforest.jello.data;

import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record GelatinComplexRecipeJsonBuilder(SpecialRecipeSerializer<?> serializer) {

    public static GelatinComplexRecipeJsonBuilder create(SpecialRecipeSerializer<?> serializer) {
        return new GelatinComplexRecipeJsonBuilder(serializer);
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        exporter.accept(new RecipeJsonProvider() {
            @Override
            public void serialize(JsonObject json) {
            }

            @Override
            public RecipeSerializer<?> getSerializer() {
                return GelatinComplexRecipeJsonBuilder.this.serializer;
            }

            @Override
            public Identifier getRecipeId() {
                return recipeId;
            }

            @Nullable
            @Override
            public JsonObject toAdvancementJson() {
                return null;
            }

            @Override
            public Identifier getAdvancementId() {
                return new Identifier("");
            }
        });
    }
}
