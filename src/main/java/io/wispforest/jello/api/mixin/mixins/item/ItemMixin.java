package io.wispforest.jello.api.mixin.mixins.item;

import io.wispforest.jello.api.mixin.ducks.entity.JelloItemExtensions;
import io.wispforest.jello.api.util.JelloItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(Item.class)
public class ItemMixin implements JelloItemExtensions {

    @Unique @Final @Mutable
    private Map<Identifier, Item> recipeSpecificRemainder;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void grabTab(Item.Settings settings, CallbackInfo ci) {
        if(settings instanceof JelloItemSettings jelloItemSettings){
            recipeSpecificRemainder = jelloItemSettings.getRecipeSpecificRemainders();
        }else{
            recipeSpecificRemainder = new HashMap<>();
        }

    }

    @Override
    public Map<Identifier, Item> getRecipeSpecificRemainder() {
        return null;
    }

    @Override
    public boolean hasRecipeSpecificRemainder() {
        return !recipeSpecificRemainder.isEmpty();
    }

    @Override
    public boolean doseRecipeHaveRemainder(Identifier identifier) {
        return this.recipeSpecificRemainder.containsKey(identifier);
    }

    @Override
    public Item getRecipeSpecificRemainder(Identifier identifier) {
        return this.recipeSpecificRemainder.get(identifier);
    }

    @Override
    public void addRecipeSpecificRemainder(Identifier recipeId) {
        this.recipeSpecificRemainder.put(recipeId, (Item)(Object)this);
    }

}
