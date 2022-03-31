package io.wispforest.jello.mixin.accessors;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.Settings.class)
public interface SettingsAccessor {
    @Accessor
    int getMaxCount();

    @Accessor
    int getMaxDamage();

    @Accessor
    Item getRecipeRemainder();

    @Accessor
    ItemGroup getGroup();

    @Accessor
    Rarity getRarity();

    @Accessor
    FoodComponent getFoodComponent();

    @Accessor
    boolean isFireproof();
}
