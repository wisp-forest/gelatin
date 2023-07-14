package io.wispforest.jello.mixins;

import com.mojang.serialization.Lifecycle;
import io.wispforest.jello.Jello;
import io.wispforest.jello.misc.DyeColorantLoader;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(net.minecraft.registry.SimpleDefaultedRegistry.class)
public abstract class SimpleDefaultedRegistry<T> extends SimpleRegistry<T> implements DefaultedRegistry<T> {

    public SimpleDefaultedRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        super(key, lifecycle);
    }

    @Shadow @NotNull public abstract T get(@Nullable Identifier id);

    @ModifyVariable(at = @At("HEAD"), method = "get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;", ordinal = 0, argsOnly = true)
    private Identifier fixMissingFromRegistry(@Nullable Identifier id) {
        if(id == null || !Objects.equals(id.getNamespace(), Jello.MODID)) return id;

        if(super.containsId(id)) return id;

        return DyeColorantLoader.remapId(id);
    }
}
