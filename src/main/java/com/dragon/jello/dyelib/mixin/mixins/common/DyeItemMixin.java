package com.dragon.jello.dyelib.mixin.mixins.common;

import com.dragon.jello.dyelib.DyeColorRegistry;
import com.dragon.jello.dyelib.mixin.ducks.DyeRedirect;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DyeItem.class)
public class DyeItemMixin implements DyeRedirect {

    @Unique @Mutable @Final private DyeColorRegistry.DyeColor color;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void fillNewDyeMap(DyeColor color, Item.Settings settings, CallbackInfo ci){
        this.color = DyeColorRegistry.DyeColor.byOldDyeColor(((DyeItem)(Object)this).getColor());

        DyeColorRegistry.DYE_COLOR_TO_DYEITEM.put(this.color, (DyeItem)(Object)this);
    }

    @Override
    public DyeColorRegistry.DyeColor getDyeColor() {
        return color;
    }
}
