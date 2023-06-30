package io.wispforest.jello.mixins;

import io.wispforest.jello.item.dyebundle.DyeBundleItem;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(BundleItem.class)
public class BundleItemMixin {

    @Inject(method = "removeFirstStack", at = @At("HEAD"), cancellable = true)
    private static void jello$removeSelectedStack(ItemStack stack, CallbackInfoReturnable<Optional<ItemStack>> cir){
        if(!(stack.getItem() instanceof DyeBundleItem)) return;

        NbtCompound bundleNbt = stack.getOrCreateNbt();

        if (!bundleNbt.has(DyeBundleItem.INVENTORY_NBT_KEY) || !bundleNbt.has(DyeBundleItem.SELECTED_STACK_NBT_KEY)) {
            cir.setReturnValue(Optional.empty());

            return;
        }

        NbtList bundleItemsList = bundleNbt.get(DyeBundleItem.INVENTORY_NBT_KEY);

        int selectedStack = stack.get(DyeBundleItem.SELECTED_STACK_NBT_KEY);

        if(selectedStack < 0 || selectedStack > bundleItemsList.size()) {
            stack.put(DyeBundleItem.SELECTED_STACK_NBT_KEY, 0);

            selectedStack = 0;
        }

        if (bundleItemsList.isEmpty() || selectedStack > bundleItemsList.size()) {
            cir.setReturnValue(Optional.empty());

            return;
        }

        NbtCompound nbtCompound2 = bundleItemsList.getCompound(selectedStack);

        ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);

        bundleItemsList.remove(selectedStack);

        if(selectedStack >= bundleItemsList.size()){
            selectedStack = selectedStack - 1;

            bundleNbt.put(DyeBundleItem.SELECTED_STACK_NBT_KEY, selectedStack);
        }

        if (bundleItemsList.isEmpty()) stack.removeSubNbt("Items");

        cir.setReturnValue(Optional.of(itemStack));
    }

    @ModifyConstant(method = "addToBundle", constant = @Constant(intValue = 0), slice =
        @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/item/BundleItem;canMergeStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/nbt/NbtList;)Ljava/util/Optional;"),
            to = @At(value = "TAIL", shift = At.Shift.BY, by = -2)
        )
    )
    private static int test(int constant, ItemStack bundle, ItemStack stack){
        if(bundle.getItem() instanceof DyeBundleItem && bundle.has(DyeBundleItem.SELECTED_STACK_NBT_KEY)){
            int selectedStack = bundle.get(DyeBundleItem.SELECTED_STACK_NBT_KEY);

            if(selectedStack < 0 || selectedStack > bundle.get(DyeBundleItem.INVENTORY_NBT_KEY).size()) {
                bundle.put(DyeBundleItem.SELECTED_STACK_NBT_KEY, 0);

                selectedStack = 0;
            }

            return selectedStack;
        }

        return constant;

    }

    @Inject(method = "addToBundle", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void jello$defaultSelectedStack(ItemStack bundle, ItemStack stack, CallbackInfoReturnable<Integer> cir, NbtCompound nbtCompound, int i, int j, int k, NbtList nbtList){
        if(bundle.getItem() instanceof DyeBundleItem && nbtList.isEmpty()){
            nbtCompound.put(DyeBundleItem.SELECTED_STACK_NBT_KEY, 0);
        }
    }
}
