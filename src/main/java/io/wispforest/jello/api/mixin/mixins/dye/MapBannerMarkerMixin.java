package io.wispforest.jello.api.mixin.mixins.dye;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.mixin.ducks.DyeRedirect;
import net.minecraft.item.DyeItem;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.text.Text;
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
    private DyeColorant color;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void dyeRegistry$setDyeColor(BlockPos pos, net.minecraft.util.DyeColor dyeColor, Text name, CallbackInfo ci){
        this.color = DyeColorant.byOldDyeColor(((DyeItem)(Object)this).getColor());
    }

    @Override
    public DyeColorant getDyeColor() {
        return color;
    }
}
