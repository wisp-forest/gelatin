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
    int gelatin$getMaxCount();

    @Accessor("maxDamage")
    int gelatin$getMaxDamage();

    @Accessor("recipeRemainder")
    Item gelatin$getRecipeRemainder();

    @Accessor("group")
    ItemGroup gelatin$getGroup();

    @Accessor("rarity")
    Rarity gelatin$getRarity();

    @Accessor("foodComponent")
    FoodComponent gelatin$getFoodComponent();

    @Accessor("fireproof")
    boolean gelatin$isFireproof();
}