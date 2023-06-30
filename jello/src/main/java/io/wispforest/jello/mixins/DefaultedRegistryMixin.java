package io.wispforest.jello.mixins;

import com.mojang.serialization.Lifecycle;
import io.wispforest.jello.Jello;
import io.wispforest.jello.misc.DyeColorantLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

@Mixin(DefaultedRegistry.class)
public abstract class DefaultedRegistryMixin <T> extends SimpleRegistry<T> {
    public DefaultedRegistryMixin(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, @org.jetbrains.annotations.Nullable Function<T, RegistryEntry.Reference<T>> valueToEntryFunction) {
        super(key, lifecycle, valueToEntryFunction);
    }

    @Shadow @NotNull public abstract T get(@org.jetbrains.annotations.Nullable Identifier id);

    @ModifyVariable(at = @At("HEAD"), method = "get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;", ordinal = 0, argsOnly = true)
    private Identifier fixMissingFromRegistry(@Nullable Identifier id) {
        if(id == null || !Objects.equals(id.getNamespace(), Jello.MODID)) return id;

        if(super.containsId(id)) return id;

        return DyeColorantLoader.remapId(id);
    }
}
