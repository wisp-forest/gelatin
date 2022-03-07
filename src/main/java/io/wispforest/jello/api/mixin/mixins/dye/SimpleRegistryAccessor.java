package io.wispforest.jello.api.mixin.mixins.dye;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Function;

@Mixin(SimpleRegistry.class)
public interface SimpleRegistryAccessor<T> {
    @Mutable
    @Accessor
    void setValueToEntryFunction(Function<T, RegistryEntry.Reference<T>> valueToEntryFunction);

    @Accessor
    void setUnfrozenValueToEntry(Map<T, RegistryEntry.Reference<T>> unfrozenValueToEntry);
}
