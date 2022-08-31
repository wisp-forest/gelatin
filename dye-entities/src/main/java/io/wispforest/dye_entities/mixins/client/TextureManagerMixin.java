package io.wispforest.dye_entities.mixins.client;

import io.wispforest.dye_entities.client.GrayScaledTexture;
import io.wispforest.dye_entities.ducks.TextureManagerDuck;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin implements TextureManagerDuck {

    @Shadow public abstract void registerTexture(Identifier id, AbstractTexture texture);

    @Shadow @Final private Map<Identifier, AbstractTexture> textures;

    @Override
    public boolean hasGrayScaledTextureBeenMade(Identifier id){
        return (AbstractTexture)this.textures.get(id) != null;
    }

    @Override
    public void createGrayScaledTexture(Identifier grayScaledId, Identifier defaultId) {
        AbstractTexture abstractTexture = (AbstractTexture)this.textures.get(grayScaledId);
        if (abstractTexture == null) {
            abstractTexture = new GrayScaledTexture(grayScaledId, defaultId);
            this.registerTexture(grayScaledId, abstractTexture);
        }
    }
}
