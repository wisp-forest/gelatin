package io.wispforest.jello.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.util.List;

public class DyeBundleDummyScreen extends Screen {

    public static final DyeBundleDummyScreen INSTANCE = new DyeBundleDummyScreen(Text.of("DEEZ NUTS"));

    protected DyeBundleDummyScreen(Text title) {
        super(title);

        this.init(MinecraftClient.getInstance(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
    }

    @Override
    protected void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y) {
        if (!components.isEmpty()) {
            int i = 0;
            int j = components.size() == 1 ? -2 : 0;

            for (TooltipComponent tooltipComponent : components) {
                int k = tooltipComponent.getWidth(this.textRenderer);
                if (k > i) {
                    i = k;
                }

                j += tooltipComponent.getHeight();
            }

            int l = x + 3; //+ 12;
            int m = y + 3; //- 12;

            l -= (i / 2);
            m -= (j / 2);

            m += ((y - 22) / 2);

            if (l + (i / 2) > this.width) {
                l -= 28 + i;
            }

            if (m + j > this.height - 22) {
                m = (this.height - j - 6) - 22;
            }


            matrices.push();
            int o = -267386864;
            int p = 1347420415;
            int q = 1344798847;
            int r = 400;
            float f = this.itemRenderer.zOffset;
            this.itemRenderer.zOffset = 400.0F;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            //Top and bottom most dark purple line
            fillGradient(matrix4f, bufferBuilder, l - 3, m - 4, l + i + 3, m - 3, 400, o, o);
            fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 3, l + i + 3, m + j + 4, 400, o, o);

            //Main Background square
            fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m + j + 3, 400, o, o);

            //Left and right dark purple lines
            fillGradient(matrix4f, bufferBuilder, l - 4, m - 3, l - 3, m + j + 3, 400, o, o);
            fillGradient(matrix4f, bufferBuilder, l + i + 3, m - 3, l + i + 4, m + j + 3, 400, o, o);

            fillGradient(matrix4f, bufferBuilder, l - 3, m - 3 + 1, l - 3 + 1, m + j + 3 - 1, 400, p, q);
            fillGradient(matrix4f, bufferBuilder, l + i + 2, m - 3 + 1, l + i + 3, m + j + 3 - 1, 400, p, q);

            fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m - 3 + 1, 400, p, p);
            fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 2, l + i + 3, m + j + 3, 400, q, q);

            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            matrices.translate(0.0, 0.0, 400.0);
            int s = m;

            for (int t = 0; t < components.size(); ++t) {
                TooltipComponent tooltipComponent2 = (TooltipComponent) components.get(t);
                tooltipComponent2.drawText(this.textRenderer, l, s, matrix4f, immediate);
                s += tooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
            }

            immediate.draw();
            matrices.pop();
            s = m;

            for (int t = 0; t < components.size(); ++t) {
                TooltipComponent tooltipComponent2 = (TooltipComponent) components.get(t);
                tooltipComponent2.drawItems(this.textRenderer, l, s, matrices, this.itemRenderer, 400);
                s += tooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
            }

            this.itemRenderer.zOffset = f;
        }
    }
}
