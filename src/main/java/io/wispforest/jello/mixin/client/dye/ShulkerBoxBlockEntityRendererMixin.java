package io.wispforest.jello.mixin.client.dye;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.misc.ducks.DyeBlockEntityStorage;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public class ShulkerBoxBlockEntityRendererMixin {

    @Unique protected DyeColorant color;

    @ModifyVariable(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;getColor()Lnet/minecraft/util/DyeColor;", shift = At.Shift.BY, by = 2))
    private DyeColor makeDyeColorNull(DyeColor dyeColor) {
        return dyeColor == DyeColorantRegistry.Constants.NULL_VALUE_OLD ? null : dyeColor;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
    private SpriteIdentifier implementCustomColors(SpriteIdentifier spriteIdentifier, ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
        DyeColorant blockEntityDyeColorant = ((DyeBlockEntityStorage) shulkerBoxBlockEntity).getDyeColor();

        if (blockEntityDyeColorant != DyeColorantRegistry.NULL_VALUE_NEW && !DyeColorantRegistry.Constants.VANILLA_DYES.contains(blockEntityDyeColorant)) {
            color = blockEntityDyeColorant;

            return TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(0);
        } else {
            color = DyeColorantRegistry.NULL_VALUE_NEW;
        }

        return spriteIdentifier;
    }

    @ModifyArgs(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void changeRenderColor(Args args) {
        if (color != DyeColorantRegistry.NULL_VALUE_NEW) {
            float[] colorComp = color.getColorComponents();

            args.set(4, colorComp[0]);
            args.set(5, colorComp[1]);
            args.set(6, colorComp[2]);
            args.set(7, 1F);
        }
    }
}
