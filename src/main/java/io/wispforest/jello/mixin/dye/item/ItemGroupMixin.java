package io.wispforest.jello.mixin.dye.item;

import io.wispforest.jello.misc.MiscItemGroup;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemGroup.class)
public class ItemGroupMixin {

    @Shadow
    @Mutable
    @Final
    public static ItemGroup MISC;

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup$8;<init>(ILjava/lang/String;)V", shift = At.Shift.BY, by = 2))
    //
    private static void testMethod(CallbackInfo ci) {
        MISC = new MiscItemGroup();
    }
}
