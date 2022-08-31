package io.wispforest.gelatin.dye_entities.mixins.client;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_registry.ducks.CustomCollarColorStorage;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfCollarFeatureRenderer.class)
public abstract class WolfCollarFeatureRendererMixin extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {

    @Shadow @Final private static Identifier SKIN;

    public WolfCollarFeatureRendererMixin(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/WolfEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void gelatin$customCollarColor(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, WolfEntity wolfEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci){
        if (wolfEntity.isTamed() && !wolfEntity.isInvisible()) {
            CustomCollarColorStorage collarColorStorage = ((CustomCollarColorStorage) wolfEntity);

            float[] fs;

            if(collarColorStorage.isRainbowCollared()){
                fs = ColorUtil.rainbowColorizer(wolfEntity, g);
            } else if (collarColorStorage.getCustomCollarColor() != DyeColorantRegistry.NULL_VALUE_NEW){
                fs = ((CustomCollarColorStorage) wolfEntity).getCustomCollarColor().getColorComponents();
            } else {
                return;
            }

            renderModel(this.getContextModel(), SKIN, matrixStack, vertexConsumerProvider, i, wolfEntity, fs[0], fs[1], fs[2]);

            ci.cancel();
        }
    }
}
