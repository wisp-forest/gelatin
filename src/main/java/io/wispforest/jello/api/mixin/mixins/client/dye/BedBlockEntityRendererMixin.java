package io.wispforest.jello.api.mixin.mixins.client.dye;

import io.wispforest.jello.api.JelloClientAPI;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BedBlockEntityRenderer.class)
public class BedBlockEntityRendererMixin {

    private DyeColorant cachedBedColor;

    @ModifyVariable(method = "render(Lnet/minecraft/block/entity/BedBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BedBlockEntity;getWorld()Lnet/minecraft/world/World;"))
    private SpriteIdentifier renderInvisibleBlanket(SpriteIdentifier spriteIdentifier, BedBlockEntity bedBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j){
        if(((DyeBlockStorage)bedBlockEntity.getCachedState().getBlock()).isBlockDyed()){
            cachedBedColor = ((DyeBlockStorage)bedBlockEntity.getCachedState().getBlock()).getDyeColor();

            SpriteIdentifier sprite = new SpriteIdentifier(TexturedRenderLayers.BEDS_ATLAS_TEXTURE, JelloClientAPI.BED_PILLOW_ONLY);
            sprite.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutout);

            return sprite;
        }else{
            cachedBedColor = DyeColorantRegistry.Constants.NULL_VALUE_NEW;
        }

        return spriteIdentifier;
    }

    @Inject(method = "renderPart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    private void renderColoredSheets(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ModelPart part, Direction direction, SpriteIdentifier sprite, int light, int overlay, boolean isFoot, CallbackInfo ci){
        if(cachedBedColor != DyeColorantRegistry.Constants.NULL_VALUE_NEW){
            VertexConsumer vertexConsumer2 = new SpriteIdentifier(TexturedRenderLayers.BEDS_ATLAS_TEXTURE, JelloClientAPI.BED_BLANKET_ONLY).getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);//identifier -> RenderLayer.getTranslucent());
            float[] colorComp = cachedBedColor.getColorComponents();
            part.render(matrices, vertexConsumer2, light, overlay, colorComp[0], colorComp[1], colorComp[2], 1.0f);
        }
    }

}
