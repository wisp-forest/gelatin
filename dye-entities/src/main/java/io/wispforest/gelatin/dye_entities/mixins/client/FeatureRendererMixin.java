package io.wispforest.gelatin.dye_entities.mixins.client;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_entities.ducks.ConstantColorEntity;
import io.wispforest.gelatin.dye_entities.ducks.DyeableEntity;
import io.wispforest.gelatin.dye_entities.ducks.GrayScaleEntity;
import io.wispforest.gelatin.dye_entities.ducks.RainbowEntity;
import io.wispforest.gelatin.dye_entities.client.utils.ColorizeBlackListRegistry;
import io.wispforest.gelatin.dye_entities.client.utils.GrayScaleEntityRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(FeatureRenderer.class)
public class FeatureRendererMixin<T extends Entity, M extends EntityModel<T>> {

    @Inject(method = "renderModel", at = @At(value = "HEAD"), cancellable = true)
    private static <T extends LivingEntity> void renderWithColor(EntityModel<T> model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T livingEntity, float red, float green, float blue, CallbackInfo ci) {
        if(isBlackListedFeature(livingEntity) || ColorizeBlackListRegistry.isBlackListed(livingEntity) || livingEntity instanceof WolfEntity) return;

        float[] colorComp;

        if (livingEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed() && !(livingEntity instanceof SheepEntity)) {
            colorComp = dyeableEntity.getDyeColor().getColorComponents();
        } else if (livingEntity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()) {
            colorComp = new Color(constantColorEntity.getConstantColor()).getRGBColorComponents(null);
        } else if (livingEntity instanceof RainbowEntity rainbowEntity && rainbowEntity.isRainbowTime()) {
            colorComp = ColorUtil.rainbowColorizer(livingEntity);
        } else {
            return;
        }

        Identifier vertexTexture = texture;

        if (((GrayScaleEntity) livingEntity).isGrayScaled(livingEntity)) {
            vertexTexture = GrayScaleEntityRegistry.INSTANCE.getOrFindTexture(livingEntity, texture);
        }

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(vertexTexture));

        model.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(livingEntity, 0.0F), colorComp[0], colorComp[1], colorComp[2], 1.0F);

        ci.cancel();
    }

    @Inject(method = "getTexture", at = @At(value = "RETURN"), cancellable = true)
    private void getGrayScaleID(T entity, CallbackInfoReturnable<Identifier> cir) {
        if(isBlackListedFeature(entity)) return;

        if (!(entity instanceof PlayerEntity) && (entity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled(entity))) {
            cir.setReturnValue(GrayScaleEntityRegistry.INSTANCE.getOrFindTexture(entity, cir.getReturnValue()));
        }
    }

    @Unique
    private static <T extends Entity> boolean isBlackListedFeature(T entity){
        return entity instanceof CatEntity;
    }

}
