package io.wispforest.jello.mixins.client.accessors;


import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Models.class)
public interface ModelsAccessor {

    @Invoker("item")
    static Model jello$item(String parent, TextureKey... requiredTextures) {
        throw new UnsupportedOperationException();
    }
}
