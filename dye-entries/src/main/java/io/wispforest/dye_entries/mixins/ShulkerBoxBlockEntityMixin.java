package io.wispforest.dye_entries.mixins;

import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.ducks.DyeBlockEntityStorage;
import io.wispforest.dye_registry.ducks.DyeBlockStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin implements DyeBlockEntityStorage {

    @Unique private DyeColorant cachedDyeColorant = null;

    @Inject(method = "<init>(Lnet/minecraft/util/DyeColor;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("TAIL"))
    private void setCachedDyeColorant1(DyeColor color, BlockPos pos, BlockState state, CallbackInfo ci){
        cachedDyeColorant = ((DyeBlockStorage) state.getBlock()).getDyeColorant();
    }

    @Inject(method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("TAIL"))
    private void setCachedDyeColorant2(BlockPos pos, BlockState state, CallbackInfo ci){
        cachedDyeColorant = ((DyeBlockStorage) state.getBlock()).getDyeColorant();
    }

    //Disallowing anyone to change the color of a shulker box as it is a static value for now
    @Override
    public void setDyeColor(DyeColorant dyeColorant) {}

    @Override
    public DyeColorant getDyeColor() {
        return this.cachedDyeColorant;
    }
}
