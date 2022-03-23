package io.wispforest.jello.api.mixin.ducks;

import com.mojang.datafixers.util.Pair;
import io.wispforest.jello.api.dye.DyeColorant;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;

import java.util.List;

public interface BannerBlockRenderCalls {

    static void jello$renderCanvas(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ModelPart canvas, SpriteIdentifier baseSprite, boolean isBanner, List<Pair<BannerPattern, DyeColorant>> patterns, boolean glint) {
        canvas.render(matrices, baseSprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid, glint), light, overlay);

        for(int i = 0; i < 17 && i < patterns.size(); ++i) {
            Pair<BannerPattern, DyeColorant> pair = (Pair)patterns.get(i);
            float[] fs = pair.getSecond().getColorComponents();
            BannerPattern bannerPattern = (BannerPattern)pair.getFirst();
            SpriteIdentifier spriteIdentifier = isBanner
                    ? TexturedRenderLayers.getBannerPatternTextureId(bannerPattern)
                    : TexturedRenderLayers.getShieldPatternTextureId(bannerPattern);
            canvas.render(matrices, spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), light, overlay, fs[0], fs[1], fs[2], 1.0F);
        }

    }
}
