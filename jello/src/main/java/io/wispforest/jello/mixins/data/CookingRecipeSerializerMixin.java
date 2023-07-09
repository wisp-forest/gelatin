package io.wispforest.jello.mixins.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CookingRecipeSerializer.class)
public abstract class CookingRecipeSerializerMixin<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {

    @Inject(method = "read(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonObject;)Lnet/minecraft/recipe/AbstractCookingRecipe;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/ItemConvertible;)V", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void jello_adjustStackWithPossibleResultAmount(Identifier identifier, JsonObject jsonObject, CallbackInfoReturnable<T> cir, String string, JsonElement jsonElement, Ingredient ingredient, String string2, Identifier identifier2, ItemStack itemStack){
        if(!jsonObject.has("resultamount")) return;

        int value = JsonHelper.getInt(jsonObject, "resultamount");

        if(value > 1) itemStack.setCount(value);
    }
}
