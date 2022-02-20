package com.dragon.jello.mixin.mixins.client;

import com.dragon.jello.common.Util.Util;
import com.dragon.jello.mixin.ducks.ConstantColorEntity;
import com.dragon.jello.mixin.ducks.DyeableEntity;
import com.dragon.jello.mixin.ducks.RainbowEntity;
import com.dragon.jello.lib.registry.ColorizeRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(SlimeOverlayFeatureRenderer.class)
public class SlimeOverlayFeatureRendererMixin<T extends LivingEntity> {

    @Unique protected Color color;
//    @Unique private VertexConsumer grayScaleCache = null;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "HEAD"))
    private void gatherRenderColor(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        float[] colorComp = new float[]{1.0F, 1.0F, 1.0F};

        if (ColorizeRegistry.isRegistered(livingEntity)) {

            if (livingEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed()) {
                colorComp = dyeableEntity.getDyeColor().getColorComponents();
            } else if(livingEntity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()){
                colorComp = new Color(constantColorEntity.getConstantColor()).getRGBColorComponents(null);
            } else if (livingEntity instanceof RainbowEntity rainbowEntity && rainbowEntity.isRainbowTime()) {
                colorComp = Util.rainbowColorizer(livingEntity, g);
            }
        }

        this.color = new Color(colorComp[0], colorComp[1], colorComp[2]);

        //----------------------------------------------------------------------------------------------------------------

//        Identifier grayScaledTexture = null;
//
//        if(livingEntity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled(livingEntity)){
//            grayScaledTexture = GrayScaleRegistry.getTexture(livingEntity);
//        }else{
//            grayScaledTexture = null;
//        }
//
//        boolean bl = MinecraftClient.getInstance().hasOutline(livingEntity) && livingEntity.isInvisible();
//        if (!livingEntity.isInvisible() || bl) {
//            if (bl) {
//                grayScaleCache = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(grayScaledTexture != null ? grayScaledTexture : ((SlimeOverlayFeatureRenderer)(Object)this).getTexture(livingEntity)));
//            } else {
//                grayScaleCache = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(grayScaledTexture != null ? grayScaledTexture : ((SlimeOverlayFeatureRenderer)(Object)this).getTexture(livingEntity)));
//            }
//        }
    }

    @ModifyArgs(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void changeRenderColor(Args args) {
        args.set(4, (float) color.getRed() / 255F);
        args.set(5, (float) color.getGreen() / 255F);
        args.set(6, (float) color.getBlue() / 255F);
        args.set(7, 1F);
    }

//    @ModifyVariable(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
//    private VertexConsumer changeRenderLayers(VertexConsumer value) {
//        return grayScaleCache;
//    }

}