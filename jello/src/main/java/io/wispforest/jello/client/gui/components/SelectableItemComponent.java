package io.wispforest.jello.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.jello.Jello;
import io.wispforest.jello.misc.pond.ItemRendererExtension;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.event.MouseDown;
import io.wispforest.owo.ui.util.Drawer;
import io.wispforest.owo.ui.util.ScissorStack;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class SelectableItemComponent extends ItemComponent {

    public static Identifier SELECTED_SLOT_TEXTURE = Jello.id("textures/gui/selected_slot_test_1.png");

    public boolean showItemTooltipOnHover = true;
    public boolean isSelected = false;

    public boolean isHovering = false;

//    public int zoneWidth = 3;

//    public EventStream<HoverRenderEvent> hoverSideEvent = HoverRenderEvent.newStream();

//    @Nullable public HoverSide currentSide = null;

    public SelectableItemComponent(ItemStack stack) {
        super(stack);

        this.showOverlay(true);

        this.mouseEnter().subscribe(() -> { this.isHovering = true; });
        this.mouseLeave().subscribe(() -> { this.isHovering = false; });
    }

    public SelectableItemComponent isSelected(boolean value){
        this.isSelected = value;

        return this;
    }

    public SelectableItemComponent showItemTooltipOnHover(boolean value){
        this.showItemTooltipOnHover = value;

        return this;
    }

//    public SelectableItemComponent adjustZoneWidth(int zoneWidth){
//        this.zoneWidth = zoneWidth;
//
//        return this;
//    }

//    public EventSource<HoverRenderEvent> hoverSideEvent(){
//        return this.hoverSideEvent.source();
//    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        matrices.push();

        // Translate to the root of the component
        matrices.translate(x, y, 0);

        //--------------------

        final boolean notSideLit = !this.itemRenderer.getModel(this.stack, null, null, 0).isSideLit();

        if (notSideLit) DiffuseLighting.disableGuiDepthLighting();

        matrices.push();

        // Scale according to component size and translate to the center
        matrices.scale(this.width / 16f, this.height / 16f, 1);
        matrices.translate(8.0, 8.0, 0.0);

        // Vanilla scaling and y inversion
        matrices.scale(16, -16, 16);

        this.itemRenderer.renderItem(this.stack, ModelTransformation.Mode.GUI, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, matrices, entityBuffers, 0);
        this.entityBuffers.draw();

        // Clean up
        matrices.pop();

        if (this.showOverlay) ((ItemRendererExtension) this.itemRenderer).renderGuiItemOverlay(matrices, MinecraftClient.getInstance().textRenderer, this.stack, 0, 0, 1);

        if (notSideLit) DiffuseLighting.enableGuiDepthLighting();

        //--------------------

        RenderSystem.enableDepthTest();

        matrices.translate(0,0,1);

        if(isHovering) {
            int color = new Color(1.0f, 1.0f, 1.0f, 0.50f).argb();

            Drawer.drawGradientRect(matrices, 0, 0, this.width(), this.height(), color, color, color, color);

//            hoverSideEvent.sink().render(this, matrices, mouseX, mouseY, partialTicks, delta);
        }

        if(isSelected){
            matrices.translate(-3,-3,1);

            RenderSystem.setShaderTexture(0, SELECTED_SLOT_TEXTURE);

            ScissorStack.drawUnclipped(() -> {
                Drawer.drawTexture(matrices, 0, 0, 22, 22, 0, 0, 22, 22, 32, 32);
            });
        }

        matrices.pop();

        //--------------------

        RenderSystem.disableDepthTest();
    }

//    @Override
//    public void update(float delta, int mouseX, int mouseY) {
//        super.update(delta, mouseX, mouseY);
//
//        if(this.isInBoundingBox(mouseX, mouseY)){
//            if(mouseX <= this.x + zoneWidth){
//                currentSide = HoverSide.LEFT;
//            } else if(mouseX >= this.x + this.width - zoneWidth){
//                currentSide = HoverSide.RIGHT;
//            } else if(currentSide != null){
//                currentSide = null;
//            }
//        }
//    }

    @Override
    public List<TooltipComponent> tooltip() {
        boolean playerHoldingNothing = true;

        if(MinecraftClient.getInstance().currentScreen instanceof HandledScreen handledScreen){
            playerHoldingNothing = handledScreen.getScreenHandler().getCursorStack().isEmpty();
        }

        return showItemTooltipOnHover && playerHoldingNothing ? getTooltipComponentsFromItemStack(this.stack) : List.of();
    }

    public static List<TooltipComponent> getTooltipComponentsFromItemStack(ItemStack stack){
        List<TooltipComponent> list = stack.getTooltip(MinecraftClient.getInstance().player, MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL)
                .stream()
                .map(Text::asOrderedText)
                .map(TooltipComponent::of)
                .collect(Collectors.toList());

        stack.getTooltipData().ifPresent(datax -> list.add(1, TooltipComponent.of(datax)));

        return list;
    }

//    public enum HoverSide {
//        LEFT,
//        RIGHT
//    }

    public interface HoverRenderEvent {
        void render(SelectableItemComponent component, MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta);

        static EventStream<HoverRenderEvent> newStream() {
            return new EventStream<>(subscribers -> (component, matrices, mouseX, mouseY, partialTicks, delta) -> {
                for (var subscriber : subscribers) {
                    subscriber.render(component, matrices, mouseX, mouseY, partialTicks, delta);
                }
            });
        }
    }

}
