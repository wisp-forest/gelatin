package io.wispforest.gelatin.dye_registry.mixins.item;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(DyeItem.class)
public class DyeItemMixin extends Item implements DyeItemStorage {

    @Mutable @Shadow @Final private static Map<DyeColor, DyeItem> DYES;

    public DyeItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void changeDYESmapType(CallbackInfo ci) {
        DYES = new HashMap<>();
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void fillNewDyeMap(DyeColor color, Settings settings, CallbackInfo ci) {
        if (color == DyeColorantRegistry.Constants.NULL_VALUE_OLD) return;

        DyeColorant dyeColorant = DyeColorant.byOldDyeColor(((DyeItem) (Object) this).getColor());

        if(dyeColorant == null) dyeColorant = DyeColorant.byName(color.getName(), DyeColorantRegistry.NULL_VALUE_NEW);

        this.setDyeColor(dyeColorant);
    }
}
