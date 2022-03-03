package com.dragon.jello.api.mixin.mixins.dye;

import com.dragon.jello.api.dye.registry.DyeColorRegistry;
import com.dragon.jello.api.dye.DyeColorant;
import com.dragon.jello.api.mixin.ducks.DyeRedirect;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DyeItem.class)
public class DyeItemMixin implements DyeRedirect {

    @Unique @Mutable @Final private DyeColorant color;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void fillNewDyeMap(net.minecraft.util.DyeColor color, Item.Settings settings, CallbackInfo ci){
        if(color != DyeColorRegistry.NULL_VALUE_OLD) {
            this.color = DyeColorant.byOldDyeColor(((DyeItem) (Object) this).getColor());

            if(this.color == null){
                DyeColorant possibleColor = DyeColorant.byName(color.getName(), null);

                if(possibleColor != null){
                    this.color = possibleColor;
                }
            }
        }

        DyeColorRegistry.DYE_COLOR_TO_DYEITEM.put(this.color, (DyeItem)(Object)this);
    }

    @Override
    public DyeColorant getDyeColor() {
        return color;
    }
}
