package io.wispforest.jello.mixins;

import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BundleItem.class)
public interface BundleItemAccessor {

    @Invoker("getBundleOccupancy")
    static int personality$getBundleOccupancy(ItemStack stack) {
        throw new UnsupportedOperationException();
    }
}
