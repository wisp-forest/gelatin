package io.wispforest.jello.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.jello.Jello;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.util.ScissorStack;
import io.wispforest.owo.util.EventStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class SelectableItemComponent extends ItemComponent {

    public static Identifier SELECTED_SLOT_TEXTURE = Jello.id("textures/gui/selected_slot_outline.png");

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
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        context.push();

        // Translate to the root of the component
        context.translate(x, y, 0);

        //--------------------

        final boolean notSideLit = !this.itemRenderer.getModel(this.stack, null, null, 0).isSideLit();

        if (notSideLit) DiffuseLighting.disableGuiDepthLighting();

        context.push();

        // Scale according to component size and translate to the center
        context.scale(this.width / 16f, this.height / 16f, 1);
        context.translate(8.0, 8.0, 0.0);

        // Vanilla scaling and y inversion
        if (notSideLit) {
            context.scale(16, -16, 16);
        } else {
            context.multiplyPositionMatrix(ITEM_SCALING);
        }

        this.itemRenderer.renderItem(this.stack, ModelTransformationMode.GUI, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, context.getMatrices(), entityBuffers, null, 0);
        this.entityBuffers.draw();

        // Clean up
        context.pop();

        context.push();

        context.translate(0,0, -180/*isSelected ? 3 : 1 */);

        if (this.showOverlay) context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, this.stack, 0, 0);

        context.pop();

        if (notSideLit) DiffuseLighting.enableGuiDepthLighting();

        //--------------------

        RenderSystem.enableDepthTest();

        context.translate(0,0,1);

        if(isHovering) {
            int color = new Color(1.0f, 1.0f, 1.0f, 0.50f).argb();

            context.drawGradientRect(0, 0, this.width(), this.height(), color, color, color, color);

//            hoverSideEvent.sink().render(this, matrices, mouseX, mouseY, partialTicks, delta);
        }

        if(isSelected){
            context.translate(-3,-3,1);

            ScissorStack.drawUnclipped(() -> {
                context.drawTexture(SELECTED_SLOT_TEXTURE,0, 0, 22, 22, 0, 0, 22, 22, 32, 32);
            });
        }

        context.pop();

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
        List<TooltipComponent> list = stack.getTooltip(MinecraftClient.getInstance().player, MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC)
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
