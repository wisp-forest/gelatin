package com.dragon.jello.api.mixin.mixins.common.accessors;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShulkerBoxBlockEntity.class)
public interface ShulkerBoxBlockEntityAccessor {
    @Invoker
    void callWriteNbt(NbtCompound nbt);
}
