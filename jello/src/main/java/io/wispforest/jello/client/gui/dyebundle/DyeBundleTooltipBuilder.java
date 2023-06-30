package io.wispforest.jello.client.gui.dyebundle;

import io.wispforest.jello.StorageInventoryScreens.StorageBackgroundComponent;
import io.wispforest.jello.client.gui.components.ItemStackBasedInventory;
import io.wispforest.jello.client.gui.components.SlotScrollContainer;
import io.wispforest.jello.item.dyebundle.DyeBundleItem;
import io.wispforest.jello.item.dyebundle.DyeBundlePackets;
import io.wispforest.owo.network.ClientAccess;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.EventSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class DyeBundleTooltipBuilder {

    public static final FlowLayout rootComponent = (FlowLayout) Containers.verticalFlow(Sizing.content(), Sizing.content()).id("better_bundle_tooltip");

    public int width = 3;
    public int height = 2;

    @Nullable
    private final DyeBundlePackets.StackFinder finder;

    public int x;
    public int y;

    private static final EventSource<?>.Subscription mouseEnter;
    private static final EventSource<?>.Subscription mouseLeave;

    public static final KeyBinding binding = new KeyBinding("", GLFW.GLFW_KEY_J, "");

    static {
        mouseEnter = rootComponent.mouseEnter().subscribe(() -> {
            DyeBundleTooltipRender.hoveringOverTooltip = true;
        });

        mouseLeave = rootComponent.mouseLeave().subscribe(() -> {
            DyeBundleTooltipRender.hoveringOverTooltip = false;
        });

        KeyBindingHelper.registerKeyBinding(binding);
    }

    public ItemStack bundleStack;

    public DyeBundleTooltipBuilder(int x, int y, ItemStack bundleStack, @Nullable DyeBundlePackets.StackFinder finder){
        this.bundleStack = bundleStack;
        this.finder = finder;

        this.x = x;
        this.y = y;

        this.build();
    }

    public void build() {
        rootComponent.zIndex(305);

        StorageBackgroundComponent component = rootComponent.childById(StorageBackgroundComponent.class, "wee_woo");

        if(component != null) rootComponent.removeChild(component);

        if(rootComponent.childById(StorageBackgroundComponent.class, "wee_woo") == null){
            rootComponent.child(0, StorageBackgroundComponent.of(width, height, true, false)
                    .id("wee_woo"));
        }

        Inventory inventory = ItemStackBasedInventory.of(bundleStack);

        int selectedStack = bundleStack.hasNbt() && bundleStack.has(DyeBundleItem.SELECTED_STACK_NBT_KEY)
                ? bundleStack.get(DyeBundleItem.SELECTED_STACK_NBT_KEY)
                : -1;

        FlowLayout parentContainer;

        if(rootComponent.childById(VerticalFlowLayout.class, "parent_container") == null){
            parentContainer = (FlowLayout) Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .id("parent_container")
                    .positioning(Positioning.absolute(7,7));

            rootComponent.child(parentContainer);
        } else {
            parentContainer = rootComponent.childById(VerticalFlowLayout.class, "parent_container");
        }

        if(rootComponent.childById(SlotScrollContainer.class, "slot_scroll_container" + inventory.size()) == null){
            parentContainer.clearChildren();

            parentContainer.child(new SlotScrollContainer(width, height, inventory, finder, selectedStack)
                    .id("slot_scroll_container" + inventory.size())
            );
        } else {
            parentContainer.childById(SlotScrollContainer.class, "slot_scroll_container" + inventory.size())
                    .updateInventory(inventory, selectedStack);
        }

        rootComponent.positioning(Positioning.absolute(x, y));

        if(DyeBundleTooltipRender.adapter.rootComponent.childById(VerticalFlowLayout.class, "better_bundle_tooltip") == null) {
            DyeBundleTooltipRender.adapter.rootComponent.child(rootComponent);
        }
    }

    public record UpdateDyeBundleTooltip(DyeBundlePackets.StackFinder bundleStackFinder, int itemStackIndex, DyeBundlePackets.SlotInteraction slotInteraction){

        @Environment(EnvType.CLIENT)
        public static void updateTooltip(UpdateDyeBundleTooltip message, ClientAccess access){
            ItemStack bundleStack = message.bundleStackFinder().getReferenceInfo(access.player()).stack();

            Inventory inventory = ItemStackBasedInventory.of(bundleStack);
            int selectedStackIndex = bundleStack.get(DyeBundleItem.SELECTED_STACK_NBT_KEY);

            SlotScrollContainer container = DyeBundleTooltipBuilder.rootComponent
                    .childById(SlotScrollContainer.class, "slot_scroll_container" + (inventory.size() - message.slotInteraction.actionAmount)); //

            if(message.slotInteraction != DyeBundlePackets.SlotInteraction.MODIFICATION) {
                container.rebuildContainer(inventory, selectedStackIndex, message.slotInteraction);
            } else {
                container.updateInventory(inventory, selectedStackIndex);
            }
        }
    }
}
