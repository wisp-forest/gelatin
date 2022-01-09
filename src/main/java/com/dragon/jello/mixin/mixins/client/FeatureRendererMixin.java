package com.dragon.jello.mixin.mixins.client;

import com.dragon.jello.Util.ColorStateManager;
import com.dragon.jello.Util.Util;
import com.dragon.jello.mixin.ducks.DyeableEntity;
import com.dragon.jello.mixin.ducks.GrayScaleEntity;
import com.dragon.jello.mixin.ducks.RainbowEntity;
import com.dragon.jello.registry.ColorizeRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderer.class)
public class FeatureRendererMixin<T extends Entity, M extends EntityModel<T>> {

    @Inject(method = "renderModel", at = @At(value = "HEAD"), cancellable = true)
    private static <T extends LivingEntity> void renderWithColor(EntityModel<T> model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T livingEntity, float red, float green, float blue, CallbackInfo ci){
        if(ColorizeRegistry.isRegistered(livingEntity)){
            if(!(livingEntity instanceof WolfEntity)){
                boolean hasCustomColor = false;
                float[] colorComp = new float[]{1.0F,1.0F,1.0F};

                if(livingEntity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled()){
                    ColorStateManager.enableGrayScale();
                }else{
                    ColorStateManager.disableGrayScale();
                }

                if(livingEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed() && !(livingEntity instanceof SheepEntity)){
                    colorComp = dyeableEntity.getDyeColor().getColorComponents();
                    hasCustomColor = true;
                }
                else if(livingEntity instanceof RainbowEntity rainbowEntity && rainbowEntity.isRainbowTime()) {
                    colorComp = Util.rainbowColorizer(livingEntity);
                    hasCustomColor = true;
                }

                if(hasCustomColor){
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
                    model.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(livingEntity, 0.0F), colorComp[0], colorComp[1], colorComp[2], 1.0F);
                    ci.cancel();
                }
            }
        }
    }
}
