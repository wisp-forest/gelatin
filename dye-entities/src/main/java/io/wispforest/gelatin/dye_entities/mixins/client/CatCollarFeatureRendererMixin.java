package io.wispforest.gelatin.dye_entities.mixins.client;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_registry.ducks.CustomCollarColorStorage;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatCollarFeatureRenderer.class)
public abstract class CatCollarFeatureRendererMixin extends FeatureRenderer<CatEntity, CatEntityModel<CatEntity>> {

    @Shadow @Final private static Identifier SKIN;

    @Shadow @Final private CatEntityModel<CatEntity> model;

    public CatCollarFeatureRendererMixin(FeatureRendererContext<CatEntity, CatEntityModel<CatEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/CatEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void gelatin$customCollarColor(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CatEntity catEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci){
        if (catEntity.isTamed() && !catEntity.isInvisible()) {
            CustomCollarColorStorage collarColorStorage = ((CustomCollarColorStorage) catEntity);

            float[] fs;

            if(collarColorStorage.isRainbowCollared()){
                fs = ColorUtil.rainbowColorizer(catEntity, g);
            } else if (collarColorStorage.getCustomCollarColor() != DyeColorantRegistry.NULL_VALUE_NEW){
                fs = ((CustomCollarColorStorage) catEntity).getCustomCollarColor().getColorComponents();
            } else {
                return;
            }

            render(this.getContextModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, catEntity, f, g, j, k, l, h, fs[0], fs[1], fs[2]);

            ci.cancel();
        }
    }
}
