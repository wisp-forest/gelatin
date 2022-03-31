package io.wispforest.jello.mixin.item;

import io.wispforest.jello.misc.ducks.JelloItemSettingsExtensions;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(Item.Settings.class)
public class ItemSettingsMixin implements JelloItemSettingsExtensions {

    @Unique
    private final Map<Identifier, Item> recipeSpecificRemainders = new HashMap<>();

    @Override
    public Map<Identifier, Item> getRecipeSpecificRemainder() {
        return recipeSpecificRemainders;
    }

    @Override
    public Item.Settings addRecipeSpecificRemainder(Identifier identifier, Item item) {
        recipeSpecificRemainders.put(identifier, item);

        return (Item.Settings) (Object) this;
    }
}
