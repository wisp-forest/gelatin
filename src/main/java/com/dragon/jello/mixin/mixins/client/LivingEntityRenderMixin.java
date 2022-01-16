package com.dragon.jello.mixin.mixins.client;

import com.dragon.jello.Util.ColorStateManager;
import com.dragon.jello.Util.Util;
import com.dragon.jello.mixin.ducks.DyeableEntity;
import com.dragon.jello.mixin.ducks.GrayScaleEntity;
import com.dragon.jello.mixin.ducks.RainbowEntity;
import com.dragon.jello.registry.ColorizeRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRenderMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Unique protected Vec3f colorComp;


    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
    private void grayscaleTest(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if(ColorizeRegistry.isRegistered(livingEntity)) {
//            ColorStateManager.GRAY_SCALE_TEST.clear();

            if (livingEntity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled()) {
                ColorStateManager.enableGrayScale();
            } else {
                ColorStateManager.disableGrayScale();
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void gatherRenderColor(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        float[] colorComp = new float[]{1.0F, 1.0F, 1.0F};

        if(ColorizeRegistry.isRegistered(livingEntity)) {
//            ColorStateManager.GRAY_SCALE_TEST.clear();

//            if(livingEntity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled()){
//                ColorStateManager.enableGrayScale();
//            }else{
//                ColorStateManager.disableGrayScale();
//            }

            if(livingEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed()){
                colorComp = dyeableEntity.getDyeColor().getColorComponents();
            }
            else if(livingEntity instanceof RainbowEntity rainbowEntity && rainbowEntity.isRainbowTime()) {
                colorComp = Util.rainbowColorizer(livingEntity, g);
            }
        }

        this.colorComp = new Vec3f(colorComp[0],colorComp[1],colorComp[2]);
    }

    @ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void changeRenderColor(Args args){
        args.set(4, colorComp.getX());
        args.set(5, colorComp.getY());
        args.set(6, colorComp.getZ());
    }
}
