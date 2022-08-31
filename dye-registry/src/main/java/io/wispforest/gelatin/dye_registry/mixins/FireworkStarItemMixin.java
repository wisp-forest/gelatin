package io.wispforest.gelatin.dye_registry.mixins;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import net.minecraft.item.FireworkStarItem;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireworkStarItem.class)
public class FireworkStarItemMixin {

    @Redirect(method = "appendColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/FireworkStarItem;getColorText(I)Lnet/minecraft/text/Text;"))
    private static Text dyeRegistry$getColorText(int color) {
        DyeColorant dyeColor = DyeColorant.byFireworkColor(color);
        return dyeColor == null
                ? Text.translatable("item.minecraft.firework_star.custom_color")
                : Text.translatable("item.minecraft.firework_star." + dyeColor.getName());
    }


}
