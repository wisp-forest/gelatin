package io.wispforest.gelatin.dye_entries.mixins.client;

import net.minecraft.client.texture.TextureStitcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(TextureStitcher.class)
public interface TextureStitcherAccessor<T extends TextureStitcher.Stitchable> {

    @Accessor("holders") List<TextureStitcher.Holder<T>> gelatin$getHolders();
}
