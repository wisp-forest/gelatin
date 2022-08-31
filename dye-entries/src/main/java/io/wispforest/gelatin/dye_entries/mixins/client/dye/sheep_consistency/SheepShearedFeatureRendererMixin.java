package io.wispforest.gelatin.dye_entries.mixins.client.dye.sheep_consistency;

import io.wispforest.gelatin.dye_entries.ducks.SheepDyeColorStorage;
import net.minecraft.entity.passive.SheepEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets = "me/crupette/sheepconsistency/client/SheepShearedFeatureRenderer")
@Pseudo
public class SheepShearedFeatureRendererMixin {

    @ModifyArgs(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lme/crupette/sheepconsistency/client/SheepShearedFeatureRenderer;method_23196(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V"))
                                                //me/crupette/sheepconsistency/client/SheepShearedFeatureRenderer.render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V
    private void changeRenderColor(Args args) {
        SheepEntity sheep = args.get(6);

        if (!(sheep.hasCustomName() && "jeb_".equals(sheep.getCustomName().getString()))) {
            float[] colorComp = SheepDyeColorStorage.getDyedColor(((SheepDyeColorStorage)sheep).getWoolDyeColor());

            args.set(13, (float) colorComp[0]);
            args.set(14, (float) colorComp[1]);
            args.set(15, (float) colorComp[2]);
        }
    }
}
