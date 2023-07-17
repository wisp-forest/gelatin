package io.wispforest.jello.client.gui.dyebundle;

import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import io.wispforest.jello.client.JelloClient;
import io.wispforest.jello.item.dyebundle.DyeBundlePackets;
import io.wispforest.jello.mixins.client.accessors.CreativeSlotAccessor;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.dyebundle.DyeBundleItem;
import io.wispforest.jello.api.HandledScreenEvents;
import io.wispforest.jello.api.OnItemstackTooltipRenderEvent;
import io.wispforest.jello.mixins.client.accessors.HandledScreenAccessor;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import javax.annotation.Nullable;

public class DyeBundleTooltipRender implements HudRenderCallback, OnItemstackTooltipRenderEvent.PreTooltipRender, ScreenEvents.AfterInit {

    public static OwoUIAdapter<FlowLayout> adapter = null;

    public static final DyeBundleTooltipRender INSTANCE = new DyeBundleTooltipRender();

    static {
        WindowResizeCallback.EVENT.register((client, window) -> {
            if (adapter == null) return;
            adapter.moveAndResize(0, 0, window.getScaledWidth(), window.getScaledHeight());
        });
    }

    private static DyeBundlePackets.SlotInfoHelper lastHoveredSlot = null;
    private static DyeBundleTooltipBuilder currentTooltip = null;

    public static boolean hoveringOverTooltip = false;
    public static boolean hoveringItemStack = false;

    public static void initEvents(){
        ScreenEvents.AFTER_INIT.register(INSTANCE);
        OnItemstackTooltipRenderEvent.PRE_TOOLTIP_RENDER.register(INSTANCE);
        HudRenderCallback.EVENT.register(INSTANCE);
    }

    private static void initializeAdapter() {
        var window = MinecraftClient.getInstance().getWindow();

        adapter = OwoUIAdapter.createWithoutScreen(0, 0, window.getScaledWidth(), window.getScaledHeight(), Containers::verticalFlow);

        adapter.inflateAndMount();
    }

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        if (adapter == null || JelloClient.DYE_BUNDLE_RESET_BIND.wasPressed()) initializeAdapter();

        if (hoveringOverTooltip || hoveringItemStack) return;

        if (DyeBundleItem.getHudTooltipHelper().getTime() > 0) {
            ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;

            ItemStack mainHandStack = playerEntity.getMainHandStack();
            ItemStack offHandStack = playerEntity.getOffHandStack();

            ItemStack stack = ItemStack.EMPTY;
            int slotIndex = 0;

            boolean offhandPosition = false;
            boolean removeTooltip = false;

            if(mainHandStack.getItem() instanceof DyeBundleItem){
                slotIndex = playerEntity.getInventory().selectedSlot;
                stack = mainHandStack;
            } else if(offHandStack.getItem() instanceof DyeBundleItem){
                slotIndex = -1;
                stack = offHandStack;

                offhandPosition = true;
            } else {
                removeTooltip = true;
            }

            boolean zeroTooltipTimer = removeTooltip || !DyeBundleItem.getHudTooltipHelper().currentSlotIndex(slotIndex);

            if(zeroTooltipTimer) DyeBundleItem.getHudTooltipHelper().setTimer(-1);

            if(zeroTooltipTimer || (currentTooltip != null && !ItemStack.areEqual(currentTooltip.bundleStack, stack))) {
                resetAndDispose();

                return;
            }

            if(currentTooltip == null){
                int slotX;
                int slotY;

                slotX = (MinecraftClient.getInstance().getWindow().getScaledWidth() / 2) - (offhandPosition ? 80 + 29 : 80 - (slotIndex * 20));
                slotY = ((MinecraftClient.getInstance().getWindow().getScaledHeight()) - (playerEntity.isCreative() ? 24 : 39));

                createBundleTooltip(slotX, slotY, new DyeBundlePackets.StackFinder(true, slotIndex), stack);
            }

            renderAdapter(context, -1, -1);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------

    @Override
    public void afterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        if (adapter == null || JelloClient.DYE_BUNDLE_RESET_BIND.wasPressed()) initializeAdapter();

        if(screen instanceof HandledScreen handledScreen) {
            ScreenMouseEvents.allowMouseClick(screen).register((screen1, mouseX, mouseY, button) -> {
                if(hoveringOverTooltip) adapter.mouseClicked(mouseX, mouseY, button);

                return !hoveringItemStack && !hoveringOverTooltip;
            });

            ScreenMouseEvents.allowMouseRelease(screen).register((screen1, mouseX, mouseY, button) -> {
                if(hoveringOverTooltip) adapter.mouseReleased(mouseX, mouseY, button);

                return !hoveringItemStack && !hoveringOverTooltip;
            });

            ScreenMouseEvents.allowMouseScroll(handledScreen).register(new DyeBundleStackScrollEvents());

            ScreenMouseEvents.allowMouseScroll(screen).register((screen1, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
                if(hoveringOverTooltip) adapter.mouseScrolled(mouseX, mouseY, verticalAmount);

                return !hoveringOverTooltip;
            });

            HandledScreenEvents.allowSlotHover(handledScreen).register((screen1, slot, pointX, pointY) -> {
                return !hoveringOverTooltip;
            });

            HandledScreenEvents.allowMouseDrag(handledScreen).register((screen1, mouseX, mouseY, button, deltaX, deltaY) -> {
                if(hoveringOverTooltip) adapter.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

                return !hoveringOverTooltip;
            });

            HandledScreenEvents.allowMouseTooltipWithCursorStack(handledScreen).register((screen1, cursorStack, slot, pointX, pointY) -> {
                return slot.getStack().getItem() instanceof DyeBundleItem
                        && cursorStack.getItem().isDyeItem();
            });

            ScreenEvents.afterRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> {
                if (!hoveringItemStack && !hoveringOverTooltip) {
                    lastHoveredSlot = null;

                    resetAndDispose();

                    return;
                }

                DyeBundleItem.getHudTooltipHelper().setTimer(-1);

                if (hoveringItemStack) hoveringItemStack = false;

                if (lastHoveredSlot != null) {
                    Slot focusedSlot = ((HandledScreen<?>) screen1).getScreenHandler().getSlot(lastHoveredSlot.slotIndex());

                    //Banned Slot Type
                    if(focusedSlot instanceof CreativeInventoryScreen.LockableSlot) return;

                    //Get the inner slot for more correct data
                    if(focusedSlot instanceof CreativeSlotAccessor slotAccessor) focusedSlot = slotAccessor.jello$getSlot();

                    ItemStack stack = focusedSlot.getStack();

                    boolean fromPlayerInv = screen instanceof CreativeInventoryScreen
                            && focusedSlot.inventory instanceof PlayerInventory;

                    int x = lastHoveredSlot.x();
                    int y = lastHoveredSlot.y();

                    if (stack.getItem() == JelloItems.DYE_BUNDLE && currentTooltip == null) {
                        //Need to use the Index for the Player Inv when within creative screen
                        var stackFinder = new DyeBundlePackets.StackFinder(fromPlayerInv, fromPlayerInv ? focusedSlot.getIndex() : focusedSlot.id);

                        DyeBundleTooltipRender.createBundleTooltip(x, y, stackFinder, stack);
                    }

                    renderAdapter(matrices, mouseX, mouseY);
                }
            });

            ScreenEvents.remove(screen).register(screen1 -> {
                hoveringItemStack = hoveringOverTooltip = false;

                resetAndDispose();
            });
        }
    }

    @Override
    public boolean onRender(Screen screen, DrawContext context, ItemStack stack, int x, int y) {
        if (currentTooltip != null && !ItemStack.areEqual(currentTooltip.bundleStack, stack)) {
            resetAndDispose();
        }

        if(stack.getItem() == JelloItems.DYE_BUNDLE && screen instanceof HandledScreen handledScreen){
            if(!stack.has(DyeBundleItem.INVENTORY_NBT_KEY) || stack.get(DyeBundleItem.INVENTORY_NBT_KEY).isEmpty()) return true;

            HandledScreenAccessor accessor = (HandledScreenAccessor)handledScreen;

            Slot slot = accessor.jello$getSlotAt(x, y);

            if(slot.getStack() == stack){
                x = slot.x + accessor.jello$getX() + 8;
                y = slot.y + accessor.jello$getY() + 8;

                if(slot instanceof CreativeSlotAccessor slotAccessor) slot = slotAccessor.jello$getSlot();

                if(slot.inventory instanceof PlayerInventory && (slot.getIndex() < 9)) y = y - 4;

                lastHoveredSlot = new DyeBundlePackets.SlotInfoHelper(x, y, slot.id);
            }

            hoveringItemStack = true;

            return false;
        }

        return !hoveringOverTooltip;
    }

    public static void resetAndDispose(){
        currentTooltip = null;

        hoveringOverTooltip = false;
        hoveringItemStack = false;
    }

    public static void renderAdapter(DrawContext context, int mouseX, int mouseY){
        adapter.render(context, mouseX, mouseY, MinecraftClient.getInstance().getTickDelta());

        adapter.globalInspector = false;
        adapter.enableInspector = false;
    }

    public static void createBundleTooltip(int x, int y, @Nullable DyeBundlePackets.StackFinder finder, ItemStack stack){
        int bundleX = x - 34; //30
        int bundleY = (y - 42) - 10;  //6

        currentTooltip = new DyeBundleTooltipBuilder(bundleX, bundleY, stack.copy(), finder);
    }

    @Nullable
    public DyeBundleTooltipBuilder getCurrentTooltip(){
        return currentTooltip;
    }
}
