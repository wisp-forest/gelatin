package io.wispforest.jello.mixin.dye.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Items.class)
public class ItemsMixin {

    @Inject(method = "createEmptyOptional", at = @At("HEAD"), cancellable = true)
    private static <T> void addDyeBundle(T of, CallbackInfoReturnable<Optional<T>> cir){
        cir.setReturnValue(Optional.of((T)ItemGroup.TOOLS));
    }
}
