package com.dragon.jello.mixin.mixins.client;

import com.dragon.jello.Util.Util;
import com.dragon.jello.mixin.ducks.ConstantColorEntity;
import com.dragon.jello.mixin.ducks.DyeableEntity;
import com.dragon.jello.mixin.ducks.GrayScaleEntity;
import com.dragon.jello.mixin.ducks.RainbowEntity;
import com.dragon.jello.registry.ColorizeRegistry;
import com.dragon.jello.registry.GrayScaleRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRenderMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Unique protected Color color;

    @Unique private Identifier grayScaleCache = null;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void gatherRenderColor(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        float[] colorComp = new float[]{1.0F, 1.0F, 1.0F};

        if(ColorizeRegistry.isRegistered(livingEntity)) {
            if(livingEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed()){
                colorComp = dyeableEntity.getDyeColor().getColorComponents();
            }else if(livingEntity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()){
                colorComp = new Color(constantColorEntity.getConstantColor()).getRGBColorComponents(null);
            }
            else if(livingEntity instanceof RainbowEntity rainbowEntity && rainbowEntity.isRainbowTime()) {
                colorComp = Util.rainbowColorizer(livingEntity, g);
            }
        }

        this.color = new Color(colorComp[0],colorComp[1],colorComp[2]);
    }

    @ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void changeRenderColor(Args args){
        args.set(4, (float)color.getRed() / 255F);
        args.set(5, (float)color.getGreen() / 255F);
        args.set(6, (float)color.getBlue() / 255F);
        args.set(7, 1F);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderLayer", at = @At(value = "HEAD"))
    private void checkForGrayScaleTexture(T entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<@Nullable RenderLayer> cir){
        if(!(entity instanceof PlayerEntity) && (entity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled(entity))){
            grayScaleCache = GrayScaleRegistry.getOrFindTexture(entity, ((LivingEntityRenderer<T,M>)(Object)this).getTexture(entity));
        }else{
            grayScaleCache = null;
        }

//        Identifier identifier = grayScaleCache != null ? grayScaleCache: ((LivingEntityRenderer)(Object)this).getTexture(entity);
//
//        if (translucent) {
//            cir.setReturnValue(RenderLayer.getItemEntityTranslucentCull(identifier));
//        } else if (showBody) {
//            cir.setReturnValue(model.getLayer(identifier));
//        } else {
//            if(showOutline){
//                cir.setReturnValue(RenderLayer.getOutline(identifier));
//            }else{
//                cir.setReturnValue(null);
//            }
//        }
    }

    @ModifyVariable(method = "getRenderLayer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier checkForGrayScaleTextureTest(Identifier value){
        if(grayScaleCache != null){
            return grayScaleCache;
        }else{
            return value;
        }
    }



}
