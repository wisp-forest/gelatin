package io.wispforest.jello.client.gui.dyebundle;

import io.wispforest.gelatin.common.events.HotbarMouseEvents;
import io.wispforest.jello.Jello;
import io.wispforest.jello.item.dyebundle.DyeBundleItem;
import io.wispforest.jello.item.dyebundle.DyeBundlePackets;
import io.wispforest.jello.mixins.client.accessors.CreativeSlotAccessor;
import io.wispforest.jello.mixins.client.accessors.HandledScreenAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.random.Random;

public class DyeBundleStackScrollEvents implements ScreenMouseEvents.AllowMouseScroll, HotbarMouseEvents.AllowMouseScroll {

    private static final ScrollAmountHolder HOLDER = new ScrollAmountHolder();

    @Override
    public boolean allowMouseScroll(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (screen instanceof HandledScreen handledScreen) {
            Slot focusedSlot = ((HandledScreenAccessor) handledScreen).jello$getFocusedSlot();

            if (focusedSlot != null && focusedSlot.hasStack()) {
                ItemStack possibleBundle = focusedSlot.getStack();

                if(!possibleBundle.isEmpty()
                        && possibleBundle.getItem() instanceof DyeBundleItem
                        && !isBundleEmpty(possibleBundle)
                        ){ //&& handledScreen.getScreenHandler().getCursorStack().isEmpty()

                    boolean fromPlayerInv = screen instanceof CreativeInventoryScreen && focusedSlot.inventory instanceof PlayerInventory && focusedSlot.getIndex() < 9;

                    if(focusedSlot instanceof CreativeSlotAccessor creativeSlot) focusedSlot = creativeSlot.jello$getSlot();

                    HOLDER.attemptSendPacketAndReset(possibleBundle, fromPlayerInv, fromPlayerInv ? focusedSlot.getIndex() : focusedSlot.id); //focusedSlot.getIndex()
                    HOLDER.add(verticalAmount, MinecraftClient.getInstance().world.getTime());

                    DyeBundlePackets.ScrollGivenBundle.scrollBundleSelection(possibleBundle, verticalAmount);

                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean allowMouseScroll(ClientPlayerEntity player, double horizontalAmount, double verticalAmount) {
        ItemStack possibleBundle = null;
        int slotIndex = -1;

        if(player.getMainHandStack().getItem() instanceof DyeBundleItem){
            possibleBundle = player.getMainHandStack();
            slotIndex = player.getInventory().selectedSlot;
        } else if(player.getOffHandStack().getItem() instanceof DyeBundleItem){
            possibleBundle = player.getOffHandStack();
        }

        if(possibleBundle != null
                && possibleBundle.getItem() instanceof DyeBundleItem dyeBundle
                && !isBundleEmpty(possibleBundle)
                && player.shouldCancelInteraction()) {

            dyeBundle.startHudTooltipTimer(slotIndex);

            HOLDER.attemptSendPacketAndReset(possibleBundle, true, slotIndex == -1 ? -2 : -1);
            HOLDER.add(verticalAmount, MinecraftClient.getInstance().world.getTime());

            DyeBundlePackets.ScrollGivenBundle.scrollBundleSelection(possibleBundle, verticalAmount);

            return false;
        }

        return true;
    }

    public static boolean isBundleEmpty(ItemStack bundleStack){
        if(!bundleStack.hasNbt()) return true;

        NbtCompound bundleStackNbt = bundleStack.getNbt();

        return !bundleStackNbt.has(DyeBundleItem.INVENTORY_NBT_KEY)
            || bundleStackNbt.get(DyeBundleItem.INVENTORY_NBT_KEY).isEmpty();
    }

    public static class ScrollAmountHolder {
        public static Random random = Random.create();

        public ItemStack currentStack = ItemStack.EMPTY;

        public int randomNumber = -3;
        public double verticalScrollAmount = 0.0;

        public long lastRecordTickTime = 0;

        public void add(double verticalAmount, long currentTickTime){
            verticalScrollAmount += verticalAmount;

            lastRecordTickTime = currentTickTime;
        }

        public void attemptSendPacketAndReset(ItemStack stack, boolean fromPlayerInv, int slotIndex){
            if(this.currentStack != stack){
                if(!isHolderEmpty()) {
                    Jello.CHANNEL.clientHandle().send(new DyeBundlePackets.ScrollGivenBundle(this.randomNumber, verticalScrollAmount));
                }

                if(slotIndex != -3) {
                    this.randomNumber = random.nextInt();
                    this.currentStack = stack;

                    Jello.CHANNEL.clientHandle().send(new DyeBundlePackets.StartStackTracking(randomNumber, fromPlayerInv, slotIndex));
                } else {
                    this.currentStack = ItemStack.EMPTY;
                }

                this.verticalScrollAmount = 0.0;
            }
        }

        public boolean isHolderEmpty(){
            return currentStack.isEmpty();
        }
    }

    public static void initClientTickEvent(){
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(!HOLDER.isHolderEmpty()){
                long tickDifference  = MinecraftClient.getInstance().world.getTime() - HOLDER.lastRecordTickTime;

                if(tickDifference / 20F > 0.2F){
                    HOLDER.attemptSendPacketAndReset(ItemStack.EMPTY, false, -3);
                }
            }
        });
    }

}
