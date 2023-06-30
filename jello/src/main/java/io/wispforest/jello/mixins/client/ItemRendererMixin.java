package io.wispforest.jello.mixins.client;

import io.wispforest.jello.misc.pond.ItemRendererExtension;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ItemRendererExtension {

    @Shadow public abstract void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel);

    @Shadow public float zOffset;
    @Nullable
    private MatrixStack cachedMatrixStack = null;

    @ModifyVariable(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;<init>()V", shift = At.Shift.BY, by = 2))
    private MatrixStack owo$replaceMatrixStackIfCached(MatrixStack matrixStack){
        return cachedMatrixStack != null ? cachedMatrixStack : matrixStack;
    }

    @Override
    public void renderGuiItemOverlay(MatrixStack matrices, TextRenderer renderer, ItemStack stack, int x, int y, int z, @Nullable String countLabel) {
        this.cachedMatrixStack = matrices;

        float prevZOffset = this.zOffset;

        // Used to offset the built-in static Z offset text is given
        this.zOffset = z - 200;

        if(this.cachedMatrixStack != null) this.cachedMatrixStack.push();

        this.renderGuiItemOverlay(renderer, stack, x, y, countLabel);

        this.zOffset = prevZOffset;

        if(this.cachedMatrixStack != null) {
            this.cachedMatrixStack.pop();

            this.cachedMatrixStack = null;
        }
    }
}
