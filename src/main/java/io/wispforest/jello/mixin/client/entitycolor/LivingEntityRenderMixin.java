package io.wispforest.jello.mixin.client.entitycolor;

import io.wispforest.jello.api.ducks.entity.ConstantColorEntity;
import io.wispforest.jello.api.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.ducks.entity.GrayScaleEntity;
import io.wispforest.jello.api.ducks.entity.RainbowEntity;
import io.wispforest.jello.api.registry.ColorizeBlackListRegistry;
import io.wispforest.jello.api.registry.GrayScaleEntityRegistry;
import io.wispforest.jello.api.util.ColorUtil;
import io.wispforest.jello.Jello;
import io.wispforest.jello.misc.ducks.TextureManagerDuck;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.texture.TextureManager;
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

    @Unique protected Color jello$color;

    @Unique private Identifier jello$grayScaleCache = null;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void gatherRenderColor(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        float[] colorComp = new float[]{1.0F, 1.0F, 1.0F};

        if (!ColorizeBlackListRegistry.isBlackListed(livingEntity) && Jello.getConfig().enableDyeingEntities) {
            if (livingEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed()) {
                colorComp = dyeableEntity.getDyeColor().getColorComponents();
            } else if (livingEntity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()) {
                colorComp = new Color(constantColorEntity.getConstantColor()).getRGBColorComponents(null);
            } else if (livingEntity instanceof RainbowEntity rainbowEntity && rainbowEntity.isRainbowTime()) {
                colorComp = ColorUtil.rainbowColorizer(livingEntity, g);
            }
        }

        this.jello$color = new Color(colorComp[0], colorComp[1], colorComp[2]);
    }

    @ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void changeRenderColor(Args args) {
        args.set(4, (float) jello$color.getRed() / 255F);
        args.set(5, (float) jello$color.getGreen() / 255F);
        args.set(6, (float) jello$color.getBlue() / 255F);
        args.set(7, 1F);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderLayer", at = @At(value = "HEAD"))
    private void checkForGrayScaleTexture(T entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<@Nullable RenderLayer> cir) {
        if (!FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment() && Jello.getConfig().enableGrayScalingOfEntities) {
            jello$grayScaleCache = null;
        }

        if (!(entity instanceof PlayerEntity) && (entity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled(entity))) {
            jello$grayScaleCache = GrayScaleEntityRegistry.INSTANCE.getOrFindTexture(entity, ((LivingEntityRenderer<T, M>) (Object) this).getTexture(entity));
        } else {
            jello$grayScaleCache = null;
        }
    }

    @ModifyVariable(method = "getRenderLayer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier checkForGrayScaleTextureTest(Identifier value) {
        if (jello$grayScaleCache != null) {
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();

            if(!((TextureManagerDuck) textureManager).hasGrayScaledTextureBeenMade(jello$grayScaleCache)) {
                ((TextureManagerDuck) textureManager).createGrayScaledTexture(jello$grayScaleCache, value);

                return value;
            } else {
                return jello$grayScaleCache;
            }
        } else {
            return value;
        }
    }


}
