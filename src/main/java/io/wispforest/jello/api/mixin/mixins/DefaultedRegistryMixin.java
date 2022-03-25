package io.wispforest.jello.api.mixin.mixins;

import io.wispforest.jello.api.JelloAPI;
import io.wispforest.jello.api.dye.registry.DyeColorantJsonTest;
import io.wispforest.jello.main.common.Jello;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(DefaultedRegistry.class)
public class DefaultedRegistryMixin {
    @ModifyVariable(at = @At("HEAD"), method = "get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;", ordinal = 0, argsOnly = true)
    private Identifier fixMissingFromRegistry(@Nullable Identifier id) {
        if(Objects.equals(id.getNamespace(), JelloAPI.MODID)){
            return new Identifier(Jello.MODID, id.getPath());
        }

        return id;
    }
}