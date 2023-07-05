package io.wispforest.jello.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import io.wispforest.jello.Jello;
import io.wispforest.jello.item.dyebundle.DyeBundlePackets;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.event.MouseDown;
import io.wispforest.owo.ui.util.Drawer;
import io.wispforest.owo.ui.util.ScissorStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Objects;

public class SlotScrollContainer extends ScrollContainer<FlowLayout> {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Nullable
    private DyeBundlePackets.StackFinder itemStackFinder;

    private Inventory inventory;

    public Component scrollToComponent = null;

    public SelectableItemComponent emptyComponent = (SelectableItemComponent) new SelectableItemComponent(ItemStack.EMPTY)
            .configure((SelectableItemComponent component) -> component.mouseDown().subscribe(getMouseDownEvent(component)))
            .showItemTooltipOnHover(false)
            .margins(Insets.of(1))
            .id("empty");

    private int slotWidth;
    private int slotHeight;

    public SlotScrollContainer(int slotWidth, int slotHeight, Inventory inventory, @Nullable DyeBundlePackets.StackFinder itemStackFinder, int selectedStack) {
        super(ScrollDirection.VERTICAL,
                Sizing.fixed((slotWidth * 18) + 11),
                Sizing.fixed(slotHeight * 18),
                Containers.verticalFlow(Sizing.content(), Sizing.content()));

        this.inventory = inventory;

        this.slotWidth = slotWidth;
        this.slotHeight = slotHeight;

        this.itemStackFinder = itemStackFinder;

        this.build(selectedStack);

        this.scrollStep(18);
        this.scrollbar((matrixStack, x1, y1, width1, height1, trackX, trackY, trackWidth, trackHeight, lastInteractTime, direction1, active) -> {
            RenderSystem.enableDepthTest();

            Scrollbar.vanilla()
                    .draw(matrixStack, x1, y1, width1, height1, trackX, trackY, trackWidth, trackHeight, lastInteractTime, direction1, active);

            RenderSystem.disableDepthTest();
        });
        this.scrollbarThiccness(8);
        this.fixedScrollbarLength(9);
    }

    @Override
    public void layout(Size space) {
        super.layout(space);

        if(scrollToComponent != null){
            PositionedRectangle intersection = this.intersection(scrollToComponent);

            if(intersection.height() != scrollToComponent.height()) {
                this.scrollTo(scrollToComponent);
            }

            scrollToComponent = null;
        }
    }

    public void build(int selectedStack){
        this.child.padding(Insets.right(10));

        FlowLayout currentRow = (FlowLayout) Containers.horizontalFlow(Sizing.content(), Sizing.fixed(18)).id("row_1");
        Component scrollToComponent = null;

        int x = 1;

        if(inventory.size() == 0){
            currentRow.child(emptyComponent);

            this.child.child(currentRow);
        }

        for (int i = 0; i < inventory.size(); i++) {

//            int finalI = i;

            SelectableItemComponent slotComponent = (SelectableItemComponent) new SelectableItemComponent(inventory.getStack(i))
//                    .configure((SelectableItemComponent component) -> {
//                        component.hoverSideEvent().subscribe((component1, matrices, mouseX, mouseY, partialTicks, delta) -> {
//                            RenderSystem.enableBlend();
//                            RenderSystem.defaultBlendFunc();
//                            RenderSystem.enableDepthTest();
//
//                            ScissorStack.drawUnclipped(() -> {
//                                int color = Color.BLUE.interpolate(Color.WHITE, 0.5f).argb();
//
//                                SelectableItemComponent selectedComponent = null;
//
//                                if(component1.currentSide == SelectableItemComponent.HoverSide.LEFT){
//                                    selectedComponent = this.childById(SelectableItemComponent.class, String.valueOf(finalI - 1));
//                                } else if(component1.currentSide == SelectableItemComponent.HoverSide.RIGHT){
//                                    selectedComponent = this.childById(SelectableItemComponent.class, String.valueOf(finalI + 1));
//                                }
//
//                                int startX = -1, startY = -1, width = 2, height = 2;
//
//                                if(selectedComponent != null){
//                                    if(!ScissorStack.isVisible(selectedComponent, matrices)){
//                                        return;
//                                    }
//
//                                    startX += selectedComponent.x() - component1.x();
//                                    startY += selectedComponent.y() - component1.y();
//
//                                    width += selectedComponent.width();
//                                    height += selectedComponent.height();
//                                } else {
//                                    startX += 0;
//                                    startY += 0;
//
//                                    width += component1.width();
//                                    height += component1.height();
//                                }
//
//                                matrices.push();
//
//                                matrices.translate(component1.x(),component1.y(), component1.zIndex() + 5);
//
//                                Drawer.drawRectOutline(matrices, startX, startY, width, height, color);
//
//                                matrices.pop();
//                            });
//
//                        });
//                    })
                    .showItemTooltipOnHover(true);

            slotComponent.mouseDown().subscribe(getMouseDownEvent(slotComponent));

            if(selectedStack >= 0 && i == selectedStack) scrollToComponent = slotComponent.isSelected(true);

            currentRow.child(
                    slotComponent
                            .margins(Insets.of(1))
                            .id(String.valueOf(i))
            );

            x++;

            if (x > slotWidth) {
                x = 1;

                this.child.child(currentRow);

                currentRow = (FlowLayout) Containers.horizontalFlow(Sizing.content(), Sizing.fixed(18))
                        .id("row_" + (MathHelper.floor((i + 1) / (float) slotWidth) + 1));
            }

            if (i + 1 == inventory.size()) this.child.child(currentRow);
        }

        if (scrollToComponent != null) this.scrollToComponent = scrollToComponent;
    }

    public SlotScrollContainer setStackFinder(DyeBundlePackets.StackFinder itemStackFinder){
        this.itemStackFinder = itemStackFinder;

        return this;
    }

    public void rebuildContainer(Inventory newInventory, int selectedSlot, DyeBundlePackets.SlotInteraction action){
        int newInvSize = newInventory.size();

        int prevRowAmount = MathHelper.ceil(this.inventory.size() / (float) this.slotWidth);
        int newRowAmount = MathHelper.ceil(newInvSize / (float) this.slotWidth);

        boolean removedSlot = false;

        FlowLayout layout = this.child.childById(FlowLayout.class, "row_" + Math.max(newRowAmount, 1));

        if(newRowAmount > prevRowAmount && layout == null){
            layout = (FlowLayout) Containers.horizontalFlow(Sizing.content(), Sizing.fixed(18)).id("row_" + newRowAmount);

            this.child.child(layout);
        } else if(newRowAmount < prevRowAmount && newRowAmount > 0){
            this.child.removeChild(this.child.childById(FlowLayout.class, "row_" + prevRowAmount));

            removedSlot = true;
        }

        if(action == DyeBundlePackets.SlotInteraction.ADDING){
            SelectableItemComponent component = (SelectableItemComponent) new SelectableItemComponent(ItemStack.EMPTY)
                    .showItemTooltipOnHover(true)
                    .margins(Insets.of(1))
                    .id(String.valueOf(newInvSize - 1));

            component.mouseDown().subscribe(getMouseDownEvent(component));

            layout.child(component);

            SelectableItemComponent emptyComponent = layout.childById(SelectableItemComponent.class, "empty");

            if(emptyComponent != null) layout.removeChild(emptyComponent);

        } else if(action == DyeBundlePackets.SlotInteraction.REMOVING && !removedSlot) {
            SelectableItemComponent component = this.child.childById(SelectableItemComponent.class, String.valueOf(newInvSize));

            layout.removeChild(component);

            if(newInvSize == 0) layout.child(emptyComponent);
        }

        this.inventory = newInventory;

        this.id("slot_scroll_container" + inventory.size());

        updateSlots(selectedSlot, false);
    }

    public SlotScrollContainer updateInventory(Inventory inventory, int selectedSlot){
        this.inventory = inventory;

        updateSlots(selectedSlot, true);

        return this;
    }

    public void updateSlots(int selectedStack, boolean scrollToSelectedComponent){
        Component scrollToComponent = null;

        for(int i = 0; i < inventory.size(); i++){
            SelectableItemComponent slotComponent = this.childById(SelectableItemComponent.class, String.valueOf(i));

            if(slotComponent == null){
                LOGGER.warn("[SlotScrollContainer]: During updateSlots() method, A slot component was found to be null, such will be skipped!");

                continue;
            }

            if(selectedStack >= 0){
                boolean bl = selectedStack == i;

                if(bl && scrollToSelectedComponent) scrollToComponent = slotComponent.parent();

                slotComponent.isSelected(bl);
            }

            slotComponent.stack(inventory.getStack(i));

            this.sizing(Sizing.fixed(((16 + 2) * slotWidth) + 11), Sizing.fixed(((slotHeight * (16 + 2)))));
        }

        if(scrollToComponent != null){
            PositionedRectangle intersection = this.intersection(scrollToComponent);

            if(intersection.height() != scrollToComponent.height()) this.scrollTo(scrollToComponent);
        }
    }

    public boolean onSlotClick(SelectableItemComponent component, ClickType type, double mouseX, double mouseY){
        if(itemStackFinder != null) {
            try {
                int slotId = Objects.equals(component.id(), "empty") ? 0 : Integer.parseInt(component.id());

                boolean clickSide = mouseX > component.x() + Math.floor(component.width() / 2f);

                ItemStack bundleStack = itemStackFinder.getReferenceInfo(MinecraftClient.getInstance().player).stack();

                LOGGER.info("----------------------------------------------------------------------------------------");
                LOGGER.info("[Finder: {}, SlotId: {}, ClickType: {}]", itemStackFinder, slotId, type);
                LOGGER.info("BundleData: {}", bundleStack.hasNbt() ? bundleStack.getNbt() : "None");

                ClientPlayerEntity player = MinecraftClient.getInstance().player;

                ItemStack clientCursorStack = player.isCreative()
                        ? player.currentScreenHandler.getCursorStack()
                        : ItemStack.EMPTY;

                Jello.CHANNEL.clientHandle().send(new DyeBundlePackets.DyeBundleStackInteraction(itemStackFinder, slotId, type, clickSide, clientCursorStack));

                return true;
            } catch (Exception ignored) {
                LOGGER.error(ignored.toString());
            }
        }

        return false;
    }

    @Nullable
    public ClickType getType(int button){
        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return ClickType.LEFT;
        } else if((button & GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            return ClickType.RIGHT;
        }

        return null;
    }

    public MouseDown getMouseDownEvent(SelectableItemComponent component){
        return (mouseX, mouseY, button) -> {
            ClickType type = getType(button);

            if(type != null) return onSlotClick(component, type, mouseX, mouseY);

            return false;
        };
    }

}
