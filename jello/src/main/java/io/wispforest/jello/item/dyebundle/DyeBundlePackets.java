package io.wispforest.jello.item.dyebundle;

import com.mojang.logging.LogUtils;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.wispforest.gelatin.dye_entries.variants.impl.VanillaItemVariants;
import io.wispforest.jello.Jello;
import io.wispforest.jello.client.gui.dyebundle.DyeBundleTooltipBuilder;
import io.wispforest.jello.mixins.BundleItemAccessor;
import io.wispforest.owo.network.ServerAccess;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DyeBundlePackets {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static SlotInfoHelper lastSlotHelper = SlotInfoHelper.EMPTY;

    public static IntObjectMap<ItemStack> stacksToScroll = new IntObjectHashMap<>();

    public record StartStackTracking(int interactionId, boolean fromPlayerInv, int stackIndex) {

        public static void startTracking(StartStackTracking packet, ServerAccess access){
            ItemStack stack;

            if(packet.stackIndex < 0){
                switch (packet.stackIndex){
                    case -1 -> stack = access.player().getMainHandStack();
                    case -2 -> stack = access.player().getOffHandStack();
                    default -> throw new InvalidStackLocationException("It seems that the location for the bundle stack that was to be scrolled is somewhere Jello can not find!");
                }
            } else {
                stack = new StackFinder(packet.fromPlayerInv, packet.stackIndex)
                        .getReferenceInfo(access.player()).stack();
            }

            stacksToScroll.put(packet.interactionId, stack);
        }
    }

    public record ScrollGivenBundle(int id, double verticalAmount){

        public static void scrollBundle(ScrollGivenBundle packet, ServerAccess access) {
            ItemStack bundleStack = stacksToScroll.remove(packet.id);

            if (bundleStack == null) {
                throw new NullPointerException("There was a attempt to Scroll a bundle stack and such wasn't found within the main map.");
            }

            if(packet.verticalAmount != 0.0) {
                scrollBundleSelection(bundleStack, packet.verticalAmount());
            }
        }

        public static void scrollBundleSelection(ItemStack bundleStack, double verticalAmount) {
            NbtCompound bundleStackNbt = bundleStack.getOrCreateNbt();

            if(!bundleStackNbt.has(DyeBundleItem.SELECTED_STACK_NBT_KEY)) {
                bundleStackNbt.put(DyeBundleItem.SELECTED_STACK_NBT_KEY, 0);

                return;
            }

            if(verticalAmount > 2){
                System.out.println("mouse scroll: " + verticalAmount);
            }

            NbtList bundleInventory = bundleStackNbt.get(DyeBundleItem.INVENTORY_NBT_KEY);

            int bundleSize = bundleInventory.size();

            int nextSelectedStack = (int) (bundleStackNbt.get(DyeBundleItem.SELECTED_STACK_NBT_KEY) + (verticalAmount % bundleSize));

            if(nextSelectedStack >= bundleSize){
                nextSelectedStack = nextSelectedStack - bundleSize;
            } else if(nextSelectedStack < 0) {
                nextSelectedStack = bundleSize + nextSelectedStack;
            }

            bundleStackNbt.put(DyeBundleItem.SELECTED_STACK_NBT_KEY, nextSelectedStack);
        }
    }

    /**
     * {@link DyeBundleStackInteraction#clickSide} is based off {@code false} representing Left
     * and {@code true} representing Right based off the middle of the given slot
     *
     * <pre>
     * |--------{ Interaction Table }---------|
     * |           | Left Click | Right Click |
     * |-----------|--------------------------|
     * | No Stack  | Take ALL   | Take Half   |
     * |-----------|--------------------------|
     * | Similar   | Merge ALL  | Merge One   |
     * |-----------|--------------------------|
     * | Different |        Swap Stacks       |
     * |-----------|--------------------------|
     * </pre>
     */
    public record DyeBundleStackInteraction(StackFinder bundleStackfinder, int innerStackIndex, ClickType type, boolean clickSide, @Nullable ItemStack clientCursorStack){
        public static void interact(DyeBundleStackInteraction message, ServerAccess access){
            var player = access.player();
            var reference = message.bundleStackfinder.getReferenceInfo(player);
            var bundleStack = reference.stack();

              if(!(bundleStack.getItem() instanceof DyeBundleItem)) return;

            var bundleList = bundleStack.get(DyeBundleItem.INVENTORY_NBT_KEY);
            int selectedStack = bundleStack.get(DyeBundleItem.SELECTED_STACK_NBT_KEY);

            boolean shouldMarkDirty = false;
            SlotInteraction interaction = null;

            //---------------------------------------------

            //This section pertains to how horrible minecraft creative mod handler is and doesn't sync the data needed
            AtomicReference<ItemStack> cursorStackToClient = new AtomicReference<>(null);

            Consumer<ItemStack> setCursorStack = !player.isCreative()
                    ? player.currentScreenHandler::setCursorStack
                    : cursorStackToClient::set;

            Supplier<ItemStack> getCursorStack = !player.isCreative()
                    ? player.currentScreenHandler::getCursorStack
                    : cursorStackToClient::get;

            ItemStack cursorStack = !player.isCreative()
                    ? player.currentScreenHandler.getCursorStack()
                    : (message.clientCursorStack);

            //---------------------------------------------

            LOGGER.info("Before { BundleData }: {}", bundleStack.hasNbt() ? bundleStack.getNbt() : "None");
            LOGGER.info("Before { CursorStack }: {} {}", cursorStack.toString(), cursorStack.hasNbt() ? "[" + cursorStack.getNbt() + "]" : "");

            NbtCompound dyeStackNbtTag = new NbtCompound();
            ItemStack dyeStack = ItemStack.EMPTY;

            if(message.innerStackIndex < bundleList.size()){
                dyeStackNbtTag = (NbtCompound) bundleList.get(message.innerStackIndex);
                dyeStack = ItemStack.fromNbt(dyeStackNbtTag);
            }

            if(cursorStack.isEmpty()){
                if(dyeStack.isEmpty()) return;

                if(message.type == ClickType.LEFT){
                    bundleList.remove(message.innerStackIndex);

                    interaction = SlotInteraction.REMOVING;
                } else {
                    int playerStackCount = MathHelper.ceil(dyeStack.getCount() / 2f);
                    int bundleStackCount = dyeStack.getCount() - playerStackCount;

                    if(bundleStackCount <= 0){
                        bundleList.remove(message.innerStackIndex);

                        interaction = SlotInteraction.REMOVING;
                    } else {
                        dyeStackNbtTag.putInt("Count", bundleStackCount);

                        interaction = SlotInteraction.MODIFICATION;
                    }

                    dyeStack.setCount(playerStackCount);
                }

                setCursorStack.accept(dyeStack);

                bundleStack.put(DyeBundleItem.INVENTORY_NBT_KEY, bundleList);

                shouldMarkDirty = true;
            } else {
                if(ItemStack.canCombine(dyeStack, cursorStack)){
                    int availableStackSpace = 64 - BundleItemAccessor.personality$getBundleOccupancy(bundleStack);

                    int cursorStackRemainder = cursorStack.getCount() - availableStackSpace;

                    if(cursorStackRemainder <= 0){
                        setCursorStack.accept(ItemStack.EMPTY);

                        dyeStack.increment(cursorStack.getCount());
                    } else {
                        cursorStack.setCount(cursorStackRemainder);

                        dyeStack.increment(availableStackSpace);

                        if(player.isCreative()) cursorStackToClient.set(cursorStack);
                    }

                    dyeStackNbtTag.putInt("Count", dyeStack.getCount());

                    interaction = SlotInteraction.MODIFICATION;

                    shouldMarkDirty = true;
                } else {
                    if(cursorStack.getItem().isDyed() && cursorStack.isIn(VanillaItemVariants.DYE.getPrimaryTag())){
                        NbtCompound cursorStackTag = new NbtCompound();

                        cursorStack.writeNbt(cursorStackTag);

                        if(!dyeStack.isEmpty()){
                            bundleList.set(message.innerStackIndex, cursorStackTag);

                            interaction = SlotInteraction.MODIFICATION;
                        } else {
                            bundleList.add(message.innerStackIndex, cursorStackTag);

                            interaction = SlotInteraction.ADDING;
                        }

                        setCursorStack.accept(dyeStack);

                        bundleStack.put(DyeBundleItem.INVENTORY_NBT_KEY, bundleList);

                        shouldMarkDirty = true;
                    } else {
                        player.sendMessage(Text.of("You can only put Dye Items within a Dye Bundle!"), true);
                    }
                }
            }

            if(interaction == SlotInteraction.REMOVING && selectedStack >= bundleList.size()){
                bundleStack.put(DyeBundleItem.SELECTED_STACK_NBT_KEY, bundleList.isEmpty() ? 0 : bundleList.size() - 1);
            }

            if(shouldMarkDirty){
                reference.inventory().markDirty();

                if(cursorStackToClient.get() == null){
                    player.currentScreenHandler.setPreviousCursorStack(ItemStack.EMPTY);
                }
//                player.currentScreenHandler.enableSyncing();

                player.currentScreenHandler.updateToClient();
                //player.currentScreenHandler.sendContentUpdates();

                cursorStack = getCursorStack.get();

                LOGGER.info("[Finder: {}, SlotId: {}, Interaction: {}]: ", message.bundleStackfinder, message.innerStackIndex, interaction);
                LOGGER.info("After BundleData: {}", bundleStack.hasNbt() ? bundleStack.getNbt() : "None");
                LOGGER.info("After { CursorStack }: {} {}", cursorStack.toString(), cursorStack.hasNbt() ? "[" + cursorStack.getNbt() + "]" : "");

                Jello.CHANNEL.serverHandle(player)
                        .send(new DyeBundleTooltipBuilder.UpdateDyeBundleTooltip(
                                message.bundleStackfinder,
                                message.innerStackIndex,
                                interaction,
                                cursorStackToClient.get()
                        ));
            }
        }
    }

    public static class InvalidStackLocationException extends RuntimeException {
        public InvalidStackLocationException(String s) {
            super(s);
        }
    }

    public enum SlotInteraction {
        ADDING(1),
        MODIFICATION(0),
        REMOVING(-1);

        public final int actionAmount;

        SlotInteraction(int actionAmount){
            this.actionAmount = actionAmount;
        }
    }

    /**
     * Record used to deal with the {@link CreativeInventoryScreen} lack of syncing
     * @param fromPlayerInv - Whether to take from the player Inv instead
     * @param index - The given index of the stack
     */
    public record StackFinder(boolean fromPlayerInv, int index) {

        public InventoryAndStackReference getReferenceInfo(PlayerEntity player) {
            Inventory inventory;
            int stackIndex;

            if (fromPlayerInv) {
                inventory = player.getInventory();
                stackIndex = this.index;
            } else {
                Slot slot = player.currentScreenHandler.getSlot(this.index);

                inventory = slot.inventory;
                stackIndex = slot.getIndex();
            }

            return new InventoryAndStackReference(inventory, stackIndex == -1
                    ? player.getInventory().offHand.get(0)
                    : inventory.getStack(stackIndex)
            );
        }
    }

    public record InventoryAndStackReference(Inventory inventory, ItemStack stack){ }

    public record SlotInfoHelper(int x, int y, int slotIndex){
        public static final SlotInfoHelper EMPTY = new SlotInfoHelper(-1, -1, -1);
    }
}
