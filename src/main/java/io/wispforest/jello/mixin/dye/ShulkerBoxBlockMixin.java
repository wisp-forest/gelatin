package io.wispforest.jello.mixin.dye;

import io.wispforest.jello.api.ducks.DyeBlockStorage;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.block.colored.ColoredShulkerBoxBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin extends BlockWithEntity {

    protected ShulkerBoxBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ShulkerBoxBlock;getItemStack(Lnet/minecraft/util/DyeColor;)Lnet/minecraft/item/ItemStack;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void jello$onBreakInjection(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci, BlockEntity blockEntity, ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
        if((ShulkerBoxBlock)(Object)this instanceof ColoredShulkerBoxBlock) {
            if (!world.isClient && player.isCreative() && !shulkerBoxBlockEntity.isEmpty()) {
                ItemStack itemStack = new ItemStack(VanillaBlockVariants.SHULKER_BOX.getColoredEntry(((DyeBlockStorage) this).getDyeColorant()));
                blockEntity.setStackNbt(itemStack);
                if (shulkerBoxBlockEntity.hasCustomName()) {
                    itemStack.setCustomName(shulkerBoxBlockEntity.getCustomName());
                }

                ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            } else {
                shulkerBoxBlockEntity.checkLootInteraction(player);
            }

            super.onBreak(world, pos, state, player);

            ci.cancel();
        }
    }
}
