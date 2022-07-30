package io.wispforest.jello.mixin.client.accessors;

import net.minecraft.client.texture.TextureStitcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(TextureStitcher.class)
public interface TextureStitcherAccessor {

    @Accessor("holders")
    Set<TextureStitcher.Holder> getHolders();
}
