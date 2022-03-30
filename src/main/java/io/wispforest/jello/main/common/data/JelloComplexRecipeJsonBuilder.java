package io.wispforest.jello.main.common.data;

import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record JelloComplexRecipeJsonBuilder(SpecialRecipeSerializer<?> serializer) {

    public static JelloComplexRecipeJsonBuilder create(SpecialRecipeSerializer<?> serializer) {
        return new JelloComplexRecipeJsonBuilder(serializer);
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        exporter.accept(new RecipeJsonProvider() {
            @Override
            public void serialize(JsonObject json) {
            }

            @Override
            public RecipeSerializer<?> getSerializer() {
                return JelloComplexRecipeJsonBuilder.this.serializer;
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
