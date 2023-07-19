package io.wispforest.jello.item.dyebundle;

import com.mojang.logging.LogUtils;
import io.wispforest.gelatin.dye_entities.ducks.Colorable;
import io.wispforest.gelatin.dye_entities.ducks.DyeEntityTool;
import io.wispforest.gelatin.dye_entities.misc.EntityColorImplementations;
import io.wispforest.gelatin.dye_entries.BlockColorManipulators;
import io.wispforest.gelatin.dye_entries.ducks.DyeBlockTool;
import io.wispforest.gelatin.dye_entries.ducks.SheepDyeColorStorage;
import io.wispforest.gelatin.dye_entries.variants.impl.VanillaItemVariants;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

public class DyeBundleItem extends BundleItem implements DyeBlockTool, DyeEntityTool {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final NbtKey.ListKey<ItemStack> INVENTORY_NBT_KEY = new NbtKey.ListKey<>("Items", NbtKey.Type.ITEM_STACK);
    public static final NbtKey<Integer> SELECTED_STACK_NBT_KEY = new NbtKey<>("SelectedStack", NbtKey.Type.INT);

    private static HudTimerHelper hudTimerHelper = new HudTimerHelper(-3);

    public DyeBundleItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        ItemStack firstStack = getSelectedStack(stack);

        return !firstStack.isEmpty() ? Text.translatable("text.jello.dye_bundle_pattern", super.getName(), firstStack.getName()) : super.getName(stack);
    }

    //----------------------------------------------------------------------------------------------------

    @Override
    public ActionResult attemptToDyeEntity(World world, PlayerEntity user, LivingEntity entity, ItemStack stack, Hand hand) {
        DyeColorant dyeColorant = getDyeColorantFromBundle(user, stack);

        if (!dyeColorant.nullColorCheck() && user.shouldCancelInteraction() && entity instanceof Colorable colorable) {
            if(EntityColorImplementations.dyeEntityEvent(colorable, dyeColorant)) {
                DyeBundleItem.dyeBundleInteraction(user.getStackInHand(hand), dyeColorant);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult attemptToDyeBlock(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand) {
        DyeColorant dyeColorant = getDyeColorantFromBundle(player, stack);

        if (!dyeColorant.nullColorCheck() && !player.shouldCancelInteraction()) {

            //TODO: Possible change this so it just Passes?
            if (!BlockColorManipulators.changeBlockColor(world, blockPos, dyeColorant, player, true)) {
                return ActionResult.FAIL;
            }

            this.afterInteraction(player, hand, dyeColorant);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    @NotNull
    public DyeColorant attemptToDyeCauldron(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand) {
        //TODO: Add advancement when doing such labeled "Ultimate Efficiency"

        return this.getDyeColorantFromBundle(player, stack);
    }

    @Override
    public void afterInteraction(PlayerEntity player, Hand hand, DyeColorant dyeColorant) {
        if(!player.getWorld().isClient() && !player.isCreative()){
            DyeBundleItem.dyeBundleInteraction(player.getStackInHand(hand), dyeColorant);
        }
    }

    @Override
    public void afterInteraction(ItemStack stack, DyeColorant dyeColorant) {
        DyeBundleItem.dyeBundleInteraction(stack, dyeColorant);
    }

    //--------------------------------

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        DyeColorant dyeColorant = getDyeColorantFromBundle(user, stack);

        if (!dyeColorant.nullColorCheck() && entity instanceof SheepEntity sheepEntity) {
            if (sheepEntity.isAlive() && !sheepEntity.isSheared() && ((SheepDyeColorStorage) sheepEntity).getWoolDyeColor() != dyeColorant) {
                sheepEntity.getWorld().playSoundFromEntity(user, sheepEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);

                if (!user.getWorld().isClient()) ((SheepDyeColorStorage) sheepEntity).setWoolDyeColor(dyeColorant);

                this.afterInteraction(user, hand, dyeColorant);

                return ActionResult.success(user.getWorld().isClient);
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return user.shouldCancelInteraction() ? super.use(world, user, hand) : TypedActionResult.pass(user.getStackInHand(hand));
    }

    //----------------------------------------------------------------------------------------------------

    public DyeColorant getDyeColorantFromBundle(PlayerEntity player, ItemStack bundleStack){
        final var firstStack = getSelectedStack(bundleStack);

        if (!firstStack.isEmpty()) return firstStack.getItem().getDyeColorant();

        return DyeColorantRegistry.NULL_VALUE_NEW;
    }

    public static void dyeBundleInteraction(ItemStack bundleStack, DyeColorant dyeColorant) {
        List<DyeBufferEntry> currentDyeBuffers = DyeBufferEntry.readDyeBufferEntries(bundleStack);

        boolean addedToExistingBuffer = false;

        for (int i = 0; i < currentDyeBuffers.size(); i++) {
            DyeBufferEntry bufferEntry = currentDyeBuffers.get(i);

            if (bufferEntry.getDyeColorant() == dyeColorant) {
                if (!bufferEntry.decrementBufferSize()) {
                    currentDyeBuffers.remove(i);

                    if(decrementSelectedStack(bundleStack)){
                        LOGGER.warn("[DyeBundleItem] It seems that a Dyebundle interaction happened but there was a issue with decrementing the stack selected leading to free dye usage!");
                    }
                }

                addedToExistingBuffer = true;

                break;
            }
        }

        if (!addedToExistingBuffer) {
            DyeBufferEntry bufferEntry = new DyeBufferEntry(dyeColorant, 7);

            currentDyeBuffers.add(bufferEntry);
        }

        DyeBufferEntry.writeDyeBufferEntries(currentDyeBuffers, bundleStack);
    }

    //----------------------------------------------------------------------------------------------------

    public static ItemStack getFirstStack(ItemStack stack) {
        if(!(stack.getItem() instanceof BundleItem)) return ItemStack.EMPTY;

        NbtCompound bundleNbt = stack.getOrCreateNbt();

        if (!bundleNbt.has(INVENTORY_NBT_KEY)) return ItemStack.EMPTY;

        NbtList bundleItemsList = bundleNbt.get(INVENTORY_NBT_KEY);

        if (bundleItemsList.isEmpty()) return ItemStack.EMPTY;

        return ItemStack.fromNbt(bundleItemsList.getCompound(0));
    }

    private static boolean decrementFirstStack(ItemStack bundleStack) {
        NbtCompound bundleNbt = bundleStack.getOrCreateNbt();
        if (!bundleNbt.has(INVENTORY_NBT_KEY)) {
            return false;
        } else {
            NbtList bundleItemsList = bundleNbt.get(INVENTORY_NBT_KEY);
            if (bundleItemsList.isEmpty()) {
                return false;
            } else {
                NbtCompound itemNbt = bundleItemsList.getCompound(0);

                int currentStackCount = itemNbt.getInt("Count") - 1;

                if (currentStackCount < 1) {
                    bundleItemsList.remove(0);
                } else {
                    itemNbt.putInt("Count", currentStackCount);
                }

                bundleNbt.put(INVENTORY_NBT_KEY, bundleItemsList);

                return true;
            }
        }
    }

    public static ItemStack getSelectedStack(ItemStack stack) {
        NbtCompound bundleNbt = stack.getOrCreateNbt();

        if (!bundleNbt.has(INVENTORY_NBT_KEY) || !bundleNbt.has(INVENTORY_NBT_KEY)) return ItemStack.EMPTY;

        NbtList bundleItemsList = bundleNbt.get(INVENTORY_NBT_KEY);

        int selectedStack = bundleNbt.get(SELECTED_STACK_NBT_KEY);

        if (bundleItemsList.isEmpty() || selectedStack > bundleItemsList.size()) return ItemStack.EMPTY;

        return ItemStack.fromNbt(bundleItemsList.getCompound(selectedStack));
    }

    private static boolean decrementSelectedStack(ItemStack bundleStack) {
        NbtCompound bundleNbt = bundleStack.getOrCreateNbt();

        if(!bundleNbt.has(INVENTORY_NBT_KEY) || !bundleNbt.has(SELECTED_STACK_NBT_KEY)) return false;

        NbtList bundleItemsList = bundleNbt.get(INVENTORY_NBT_KEY);

        int selectedStack = bundleNbt.get(SELECTED_STACK_NBT_KEY);

        if (bundleItemsList.isEmpty() || selectedStack > bundleItemsList.size()) return false;

        NbtCompound itemNbt = bundleItemsList.getCompound(selectedStack);

        int currentStackCount = itemNbt.getInt("Count") - 1;

        if (currentStackCount < 1) {
            bundleItemsList.remove(selectedStack);

            if(selectedStack >= bundleItemsList.size()){
               selectedStack = selectedStack - 1;

               bundleNbt.put(SELECTED_STACK_NBT_KEY, selectedStack);
            }
        } else {
            itemNbt.putInt("Count", currentStackCount);
        }

        bundleNbt.put(INVENTORY_NBT_KEY, bundleItemsList);

        return true;
    }

    //----------------------------------------------------------------------------------------------------

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        ItemStack slotStack = slot.getStack();

        if(slotStack.isEmpty() || (slotStack.getItem().isDyed() && slotStack.isIn(VanillaItemVariants.DYE.getPrimaryTag()))){
            return super.onStackClicked(stack, slot, clickType, player);
        }

        return false;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if(otherStack.isEmpty() || (otherStack.getItem().isDyed() && otherStack.isIn(VanillaItemVariants.DYE.getPrimaryTag()))){
            return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
        }

        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient && entity instanceof PlayerEntity player) {
            if (hudTimerHelper.getTime() != 0 && hudTimerHelper.currentSlotIndex(slot)) {
                hudTimerHelper.decrementTimer(player.getMainHandStack() == stack || player.getOffHandStack() == stack ? 1 : 2);
            }
        }
    }

    //--------------------------------------------

    public void startHudTooltipTimer(int slotIndex) {
        hudTimerHelper = new HudTimerHelper(slotIndex);
    }

    public static HudTimerHelper getHudTooltipHelper() {
        return hudTimerHelper;
    }

    public static final class HudTimerHelper {

        private int tickCounter;
        private final int slotIndex;

        public HudTimerHelper(int slotIndex) {
            this.slotIndex = slotIndex;
            this.tickCounter = 100;
        }

        public int slotIndex() {
            return slotIndex;
        }

        public int getTime(){
            return tickCounter;
        }

        public void decrementTimer(int amount){
            this.tickCounter -= amount;
        }

        public void setTimer(int timer){
            this.tickCounter = timer;
        }

        public boolean currentSlotIndex(int slot){
            return this.slotIndex == slot;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (HudTimerHelper) obj;
            return this.slotIndex == that.slotIndex;
        }

        @Override
        public int hashCode() {
            return Objects.hash(slotIndex);
        }

        @Override
        public String toString() {
            return "HudTimerHelper[" +
                    "slotIndex=" + slotIndex + ']';
        }


    }
}
