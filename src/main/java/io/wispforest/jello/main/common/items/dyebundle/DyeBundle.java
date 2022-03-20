package io.wispforest.jello.main.common.items.dyebundle;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.events.ColorBlockEventMethods;
import io.wispforest.jello.api.dye.events.ColorEntityEvent;
import io.wispforest.jello.api.mixin.ducks.entity.ConstantColorEntity;
import io.wispforest.jello.api.mixin.ducks.DyeItemStorage;
import io.wispforest.jello.api.mixin.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.api.registry.ColorizeRegistry;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.common.data.tags.JelloTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DyeBundle extends BundleItem {

    private static int tooltipTickCounter = 0;

    private List<DyeBufferEntry> currentBufferEntrys = new ArrayList<>();
    private int selectedSlotNumber;

    private static final String SLOT_SELECTED_KEY = "selected_slot";

    public DyeBundle(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        ItemStack firstStack = getFirstStack(stack);

        return firstStack != null ? new TranslatableText("text.jello.dye_bundle_pattern", super.getName(), firstStack.getName()) : super.getName(stack);
    }

    //----------------------------------------------------------------------------------------------------

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        attemptShuffleItemsPacket(user.getWorld());

        DyeItem firstDyeItem = (DyeItem)getFirstStack(stack).getItem();

        if(firstDyeItem != null) {
            DyeColorant dyeColorant = ((DyeItemStorage)firstDyeItem).getDyeColor();

            if (Jello.MAIN_CONFIG.enableDyeingEntitys || (entity instanceof PlayerEntity && Jello.MAIN_CONFIG.enableDyeingPlayers)) {
                if (ColorizeRegistry.isRegistered(entity)) {
                    if ((entity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored())) {
                        return ActionResult.PASS;
                    }

                    if (user.shouldCancelInteraction()) {
                        if (entity instanceof DyeableEntity dyeableEntity) {
                            return ColorEntityEvent.dyeEntityEvent(user, hand, dyeableEntity, dyeColorant);
                        }
                    }
                }
            }

            //TODO: Get new Dye Color Registry working with sheep!!!!
//            if (entity instanceof SheepEntity) {
//                SheepEntity sheepEntity = (SheepEntity) entity;
//                if (sheepEntity.isAlive() && !sheepEntity.isSheared() && sheepEntity.getColor() != dyeColorant) {
//                    sheepEntity.world.playSoundFromEntity(user, sheepEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
//                    if (!user.world.isClient) {
//                        sheepEntity.setColor(dyeColorant);
//                        stack.decrement(1);
//                    }
//
//                    return ActionResult.success(user.world.isClient);
//                }
//            }
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        ItemStack bundleStack = context.getStack();
        World world = context.getWorld();

        attemptShuffleItemsPacket(world);

        if(Jello.MAIN_CONFIG.enableDyeingBlocks && player != null) {
            DyeItem firstDyeItem = (DyeItem)getFirstStack(bundleStack).getItem();

            if(firstDyeItem != null) {
                DyeColorant dyeColorant = ((DyeItemStorage)firstDyeItem).getDyeColor();

                if (dyeColorant.isIn(JelloTags.DyeColor.VANILLA_DYES) && player.shouldCancelInteraction()) {
                    BlockState blockState = world.getBlockState(context.getBlockPos());

                    if (!ColorBlockEventMethods.changeBlockColor(world, context.getBlockPos(), blockState, ColorBlockRegistry.getVariant(blockState.getBlock(), dyeColorant), player)) {
                        return ActionResult.FAIL;
                    }

                    world.playSound(player, context.getBlockPos(), blockState.getBlock().getSoundGroup(blockState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if(!player.getAbilities().creativeMode) {
                        dyeBundleInteraction(bundleStack, dyeColorant);
                    }

                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        attemptShuffleItemsPacket(world);

        if(!user.shouldCancelInteraction()){
            return super.use(world, user, hand);
        }
        else{
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }

    public static void dyeBundleInteraction(ItemStack bundleStack, DyeColorant dyeColorant){
        List<DyeBufferEntry> currentDyeBuffers = DyeBufferEntry.readDyeBufferEntrys(bundleStack);

        boolean doseBufferExist = false;

        if(!currentDyeBuffers.isEmpty()) {
            for (int i = 0; i < currentDyeBuffers.size(); i++) {
                DyeBufferEntry bufferEntry = currentDyeBuffers.get(i);

                if (bufferEntry.getDyeColorant() == dyeColorant) {
                    if (!bufferEntry.decrementBufferSize()) {
                        currentDyeBuffers.remove(i);

                        decrementFirstStack(bundleStack);
                    }

                    doseBufferExist = true;

                    break;
                }
            }
        }

        if(!doseBufferExist){
            DyeBufferEntry bufferEntry = new DyeBufferEntry(dyeColorant, 7);

            currentDyeBuffers.add(bufferEntry);
        }

        DyeBufferEntry.writeDyeBufferEntrys(currentDyeBuffers, bundleStack);
    }

    //----------------------------------------------------------------------------------------------------

    public ItemStack getFirstStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return null;
        } else {
            NbtList nbtList = nbtCompound.getList("Items", 10);
            if (nbtList.isEmpty()) {
                return null;
            } else {
                NbtCompound nbtCompound2 = nbtList.getCompound(0);
                return ItemStack.fromNbt(nbtCompound2);
            }
        }
    }

    private static boolean decrementFirstStack(ItemStack bundleStack) {
        NbtCompound nbtCompound = bundleStack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return false;
        } else {
            NbtList nbtList = nbtCompound.getList("Items", 10);
            if (nbtList.isEmpty()) {
                return false;
            } else {
                NbtCompound nbtCompound2 = nbtList.getCompound(0);

                int currentStackCount = nbtCompound2.getInt("Count") - 1;

                if(currentStackCount < 0) {
                    nbtList.remove(0);
                }else{
                    nbtCompound2.putInt("Count", currentStackCount);
                }

                nbtCompound.put("Items", nbtList);

                return true;
            }
        }
    }

    //----------------------------------------------------------------------------------------------------

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        return slot.getStack().getItem() instanceof DyeItem
                && super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        return (!otherStack.isEmpty() && otherStack.getItem() instanceof DyeItem)
                && super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    private static void attemptShuffleItemsPacket(World world){
        if(world.isClient && DyeBundleScreenEvent.getCachedVerticalScroll() != 0F) {
            //System.out.println("Sending update packet for bundle to the server");
            Jello.CHANNEL.clientHandle().send(new DyeBundlePackets.ScreenScrollPacket(DyeBundleScreenEvent.getCachedVerticalScroll()));

            DyeBundleScreenEvent.resetCachedVerticalScroll();
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(world.isClient && entity instanceof PlayerEntity player){
            if(tooltipTickCounter != 0){
                ItemStack possibleBundle = player.getMainHandStack();

                if(possibleBundle.getItem() instanceof DyeBundle){
                    tooltipTickCounter--;
                }
                else{
                    tooltipTickCounter -= 2;
                }
            }

            attemptShuffleItemsPacket(world);
        }
    }

    public void startTooltipTimer(){
        tooltipTickCounter = 100;
    }

    public static int getTooltipTime(){
        return tooltipTickCounter;
    }
}
