package io.wispforest.jello.mixin.client.accessors;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;

@Mixin(SpriteAtlasTexture.Data.class)
public interface SpriteAtlasTextureDataAccessor {

    @Accessor("spriteIds")
    Set<Identifier> jello$getSpriteIds();

    @Accessor("width")
    int jello$getWidth();

    @Accessor("height")
    int jello$getHeight();

    @Accessor("maxLevel")
    int jello$getMaxLevel();

    @Accessor("sprites")
    List<Sprite> jello$getSprites();

}
