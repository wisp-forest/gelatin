package com.dragon.jello.mixin.mixins.client.accessors;

import net.minecraft.data.client.model.Model;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.TextureKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Models.class)
public interface ModelsAccessor {
    @Invoker
    static Model callItem(String parent, TextureKey... requiredTextures) {
        throw new UnsupportedOperationException();
    }
}
