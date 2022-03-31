package io.wispforest.jello.mixin;

import io.wispforest.jello.misc.ducks.entity.JelloItemExtensions;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Recipe.class)
public interface RecipeMixin<C extends Inventory> {

    @Shadow
    Identifier getId();

    @Inject(method = "getRemainder", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addRecipeSpecificRemainder(C inventory, CallbackInfoReturnable<DefaultedList<ItemStack>> cir, DefaultedList<ItemStack> defaultedList) {
        for (int i = 0; i < defaultedList.size(); ++i) {
            Item item = inventory.getStack(i).getItem();
            if (((JelloItemExtensions) item).hasRecipeSpecificRemainder()) {
                if (((JelloItemExtensions) item).doseRecipeHaveRemainder(this.getId())) {
                    if (defaultedList.get(i) == ItemStack.EMPTY) {
                        defaultedList.set(i, new ItemStack(((JelloItemExtensions) item).getRecipeSpecificRemainder(this.getId())));
                    }
                }
            }
        }
    }
}
