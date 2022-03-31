package io.wispforest.jello.mixin.dye;

import io.wispforest.jello.api.dye.DyeColorant;
import net.minecraft.item.FireworkStarItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireworkStarItem.class)
public class FireworkStarItemMixin {

    @Redirect(method = "appendColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/FireworkStarItem;getColorText(I)Lnet/minecraft/text/Text;"))
    private static Text dyeRegistry$getColorText(int color) {
        DyeColorant dyeColor = DyeColorant.byFireworkColor(color);
        return dyeColor == null
                ? new TranslatableText("item.minecraft.firework_star.custom_color")
                : new TranslatableText("item.minecraft.firework_star." + dyeColor.getName());
    }


}
