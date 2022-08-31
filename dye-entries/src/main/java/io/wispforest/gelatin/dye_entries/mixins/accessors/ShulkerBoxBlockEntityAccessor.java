package io.wispforest.gelatin.dye_entries.mixins.accessors;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShulkerBoxBlockEntity.class)
public interface ShulkerBoxBlockEntityAccessor {
    @Invoker("writeNbt")
    void gelatin$callWriteNbt(NbtCompound nbt);
}
