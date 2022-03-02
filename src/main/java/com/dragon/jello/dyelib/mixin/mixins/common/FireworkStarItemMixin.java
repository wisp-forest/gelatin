package com.dragon.jello.dyelib.mixin.mixins.common;

import com.dragon.jello.dyelib.DyeColorRegistry;
import net.minecraft.item.FireworkStarItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireworkStarItem.class)
public class FireworkStarItemMixin {

    @Redirect(method = "appendColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/FireworkStarItem;getColorText(I)Lnet/minecraft/text/Text;"))
    private static Text dyeRegistry$getColorText(int color){
        DyeColorRegistry.DyeColor dyeColor = DyeColorRegistry.BY_FIREWORK_COLOR.get(color);
        return dyeColor == null
                ? new TranslatableText("item.minecraft.firework_star.custom_color")
                : new TranslatableText("item.minecraft.firework_star." + dyeColor.getName());
    }


}
