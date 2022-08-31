package io.wispforest.dye_entries.mixins.client.dye;

import io.wispforest.common.util.ColorUtil;
import io.wispforest.dye_entries.ducks.SheepDyeColorStorage;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepWoolFeatureRenderer.class)
public abstract class SheepWoolFeatureRendererMixin extends FeatureRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {

    public SheepWoolFeatureRendererMixin(FeatureRendererContext<SheepEntity, SheepEntityModel<SheepEntity>> context) {
        super(context);
    }

    @Shadow
    public abstract void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntity sheepEntity, float f, float g, float h, float j, float k, float l);

    @Shadow
    @Final
    private SheepWoolEntityModel<SheepEntity> model;

    @Shadow
    @Final
    private static Identifier SKIN;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;hasCustomName()Z"), cancellable = true)
    private void renderNewDyeColors(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntity sheepEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        float s;
        float t;
        float u;
        if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().getString())) {
            float[] colorComp = ColorUtil.rainbowColorizer(sheepEntity, g);
            s = colorComp[0];
            t = colorComp[1];
            u = colorComp[2];
        } else {
            float[] hs = SheepDyeColorStorage.getDyedColor(((SheepDyeColorStorage)sheepEntity).getWoolDyeColor());
            s = hs[0];
            t = hs[1];
            u = hs[2];
        }

        render(this.getContextModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, sheepEntity, f, g, j, k, l, h, s, t, u);

        ci.cancel();
    }
}
