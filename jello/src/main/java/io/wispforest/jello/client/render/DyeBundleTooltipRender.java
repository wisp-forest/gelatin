package io.wispforest.jello.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.jello.mixins.client.accessors.ScreenAccessor;
import io.wispforest.jello.item.dyebundle.DyeBundleItem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;

public class DyeBundleTooltipRender implements HudRenderCallback {

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        if (DyeBundleItem.getTooltipTime() != 0) {
            ItemStack mainHandStack = MinecraftClient.getInstance().player.getMainHandStack();

            if (!(mainHandStack.getItem() instanceof DyeBundleItem)) {
                return;
            }

            DyeBundleDummyScreen.INSTANCE.resize(MinecraftClient.getInstance(),
                    MinecraftClient.getInstance().getWindow().getScaledWidth(),
                    MinecraftClient.getInstance().getWindow().getScaledHeight());

            //MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier("minecraft:item/apple"));

            ((ScreenAccessor) DyeBundleDummyScreen.INSTANCE).jello$renderTooltip(matrixStack,
                    mainHandStack,
                    MinecraftClient.getInstance().getWindow().getScaledWidth() / 2,
                    MinecraftClient.getInstance().getWindow().getScaledHeight() / 2);

            //TooltipComponent dyeBundleTooltipComp = TooltipComponent.of(mainHandStack.getItem().getTooltipData(mainHandStack).get());

            //((ScreenAccessor)DyeBundleDummyScreen.INSTANCE).jello$renderTooltipFromComponents(matrixStack, List.of(dyeBundleTooltipComp), 0,0);
            //this.renderTooltipFromComponents(matrixStack, dyeBundleTooltipComp, MinecraftClient.getInstance().getWindow().getScaledWidth() / 2,MinecraftClient.getInstance().getWindow().getScaledHeight() / 2);

//            dyeBundleTooltipComp.drawText(MinecraftClient.getInstance().textRenderer,
//                    MinecraftClient.getInstance().getWindow().getScaledWidth() / 2,
//                    MinecraftClient.getInstance().getWindow().getScaledHeight() / 2,
//                    matrixStack.peek().getPositionMatrix(), MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers());

//            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
//
//            float f = itemRenderer.zOffset;
//            itemRenderer.zOffset = 400.0F;
//
//            dyeBundleTooltipComp.drawItems(MinecraftClient.getInstance().textRenderer,
//                    MinecraftClient.getInstance().getWindow().getScaledWidth() / 2,
//                    MinecraftClient.getInstance().getWindow().getScaledHeight() / 2, matrixStack,
//                    MinecraftClient.getInstance().getItemRenderer(), 400);
//
//            itemRenderer.zOffset = f;
        }
    }

    private void renderTooltipFromComponents(MatrixStack matrices, TooltipComponent component, int x, int y) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();


        int i = component.getWidth(textRenderer);
        int j = component.getHeight();

        int l = x + 12;
        int m = y - 12;

        matrices.push();
        int o = -267386864;
        int p = 1347420415;
        int q = 1344798847;
        int r = 400;
        float f = itemRenderer.zOffset;
        itemRenderer.zOffset = 400.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        fillGradient(matrix4f, bufferBuilder, l - 3, m - 4, l + i + 3, m - 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 3, l + i + 3, m + j + 4, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m + j + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l - 4, m - 3, l - 3, m + j + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l + i + 3, m - 3, l + i + 4, m + j + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, l - 3, m - 3 + 1, l - 3 + 1, m + j + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(matrix4f, bufferBuilder, l + i + 2, m - 3 + 1, l + i + 3, m + j + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m - 3 + 1, 400, 1347420415, 1347420415);
        fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 2, l + i + 3, m + j + 3, 400, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.end();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);
        int s = m;


        component.drawText(textRenderer, l, s, matrix4f, immediate);
        s += component.getHeight() + 2;


        immediate.draw();
        matrices.pop();
        s = m;


        component.drawItems(textRenderer, l, s, matrices, itemRenderer, 400);
        s += component.getHeight() + 2;


        itemRenderer.zOffset = f;

    }

    protected static void fillGradient(Matrix4f matrix, BufferBuilder builder, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        float f = (float) (colorStart >> 24 & 0xFF) / 255.0F;
        float g = (float) (colorStart >> 16 & 0xFF) / 255.0F;
        float h = (float) (colorStart >> 8 & 0xFF) / 255.0F;
        float i = (float) (colorStart & 0xFF) / 255.0F;
        float j = (float) (colorEnd >> 24 & 0xFF) / 255.0F;
        float k = (float) (colorEnd >> 16 & 0xFF) / 255.0F;
        float l = (float) (colorEnd >> 8 & 0xFF) / 255.0F;
        float m = (float) (colorEnd & 0xFF) / 255.0F;
        builder.vertex(matrix, (float) endX, (float) startY, (float) z).color(g, h, i, f).next();
        builder.vertex(matrix, (float) startX, (float) startY, (float) z).color(g, h, i, f).next();
        builder.vertex(matrix, (float) startX, (float) endY, (float) z).color(k, l, m, j).next();
        builder.vertex(matrix, (float) endX, (float) endY, (float) z).color(k, l, m, j).next();
    }
}
