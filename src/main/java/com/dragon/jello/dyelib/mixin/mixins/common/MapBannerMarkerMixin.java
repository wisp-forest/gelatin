package com.dragon.jello.dyelib.mixin.mixins.common;

import com.dragon.jello.dyelib.DyeColorRegistry;
import com.dragon.jello.dyelib.mixin.ducks.DyeRedirect;
import net.minecraft.item.DyeItem;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapBannerMarker.class)
public class MapBannerMarkerMixin implements DyeRedirect {

    @Unique
    @Mutable
    @Final
    private DyeColorRegistry.DyeColor color;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void dyeRegistry$setDyeColor(BlockPos pos, DyeColor dyeColor, Text name, CallbackInfo ci){
        this.color = DyeColorRegistry.DyeColor.byOldDyeColor(((DyeItem)(Object)this).getColor());
    }

    @Override
    public DyeColorRegistry.DyeColor getDyeColor() {
        return color;
    }
}
