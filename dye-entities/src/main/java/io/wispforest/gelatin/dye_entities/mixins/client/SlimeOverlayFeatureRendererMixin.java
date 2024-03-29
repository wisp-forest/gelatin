package io.wispforest.gelatin.dye_entities.mixins.client;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_entities.client.utils.ColorizeBlackListRegistry;
import io.wispforest.gelatin.dye_entities.ducks.Colored;
import net.minecraft.client.MinecraftClient;
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

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "HEAD"))
    private void gatherRenderColor(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        float[] colorComp = new float[]{1.0F, 1.0F, 1.0F};

        if(!ColorizeBlackListRegistry.isBlackListed(livingEntity) && livingEntity instanceof Colored colored && (colored.isRainbow() || colored.isColored())){
            colorComp = ColorUtil.getColorComponents(colored.getColor(MinecraftClient.getInstance().getTickDelta()));
        }

//        if (!ColorizeBlackListRegistry.isBlackListed(livingEntity)) {
//            if (livingEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed()) {
//                colorComp = dyeableEntity.getDyeColor().getColorComponents();
//            } else if (livingEntity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()) {
//                colorComp = new Color(constantColorEntity.getConstantColor()).getRGBColorComponents(null);
//            } else if (livingEntity instanceof RainbowEntity rainbowEntity && rainbowEntity.isRainbowTime()) {
//                colorComp = ColorUtil.rainbowColorizerComp(livingEntity, g);
//            }
//        }

        this.color = new Color(colorComp[0], colorComp[1], colorComp[2]);
    }

    @ModifyArgs(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void changeRenderColor(Args args) {
        args.set(4, (float) color.getRed() / 255F);
        args.set(5, (float) color.getGreen() / 255F);
        args.set(6, (float) color.getBlue() / 255F);
        args.set(7, 1F);
    }
}