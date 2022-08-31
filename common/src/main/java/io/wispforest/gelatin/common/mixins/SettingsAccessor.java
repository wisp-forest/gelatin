package io.wispforest.gelatin.common.mixins;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.Settings.class)
public interface SettingsAccessor {

    @Accessor("maxCount")
    int jello$getMaxCount();

    @Accessor("maxDamage")
    int jello$getMaxDamage();

    @Accessor("recipeRemainder")
    Item jello$getRecipeRemainder();

    @Accessor("group")
    ItemGroup jello$getGroup();

    @Accessor("rarity")
    Rarity jello$getRarity();

    @Accessor("foodComponent")
    FoodComponent jello$getFoodComponent();

    @Accessor("fireproof")
    boolean jello$isFireproof();
}