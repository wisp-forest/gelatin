package io.wispforest.jello.item.dyebundle;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.events.ColorBlockEventMethods;
import io.wispforest.jello.api.dye.events.ColorEntityEvent;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.api.registry.ColorizeRegistry;
import io.wispforest.jello.misc.ducks.DyeItemStorage;
import io.wispforest.jello.misc.ducks.SheepDyeColorStorage;
import io.wispforest.jello.misc.ducks.entity.ConstantColorEntity;
import io.wispforest.jello.misc.ducks.entity.DyeableEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class DyeBundleItem extends BundleItem {

    private static int tooltipTickCounter = 0;

    public DyeBundleItem(Settings settings) {
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

        ItemStack firstDyeStack = getFirstStack(stack);

        if (firstDyeStack.getItem() instanceof DyeItem dyeItem) {
            DyeColorant dyeColorant = ((DyeItemStorage) dyeItem).getDyeColorant();

            if (Jello.getConfig().enableDyeingEntities || (entity instanceof PlayerEntity && Jello.getConfig().enableDyeingPlayers)) {
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

            if (entity instanceof SheepEntity sheepEntity) {
                if (sheepEntity.isAlive() && !sheepEntity.isSheared() && ((SheepDyeColorStorage) sheepEntity).getWoolDyeColor() != dyeColorant) {
                    sheepEntity.world.playSoundFromEntity(user, sheepEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    if (!user.world.isClient) {
                        ((SheepDyeColorStorage) sheepEntity).setWoolDyeColor(dyeColorant);
                        DyeBundleItem.dyeBundleInteraction(firstDyeStack, dyeColorant);
                    }

                    return ActionResult.success(user.world.isClient);
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        ItemStack bundleStack = context.getStack();
        World world = context.getWorld();

        attemptShuffleItemsPacket(world);

        if (Jello.getConfig().enableDyeingBlocks && player != null) {
            final var firstStack = getFirstStack(bundleStack);
            if (firstStack.isEmpty()) return ActionResult.PASS;

            DyeItem firstDyeItem = (DyeItem) firstStack.getItem();

            if (firstDyeItem != null) {
                DyeColorant dyeColorant = ((DyeItemStorage) firstDyeItem).getDyeColorant();

                if (!player.shouldCancelInteraction()) {
                    BlockState blockState = world.getBlockState(context.getBlockPos());

                    if (!ColorBlockEventMethods.changeBlockColor(world, context.getBlockPos(), blockState, ColorBlockRegistry.getVariant(blockState.getBlock(), dyeColorant), player)) {
                        return ActionResult.FAIL;
                    }

                    world.playSound(player, context.getBlockPos(), blockState.getBlock().getSoundGroup(blockState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if (!player.getAbilities().creativeMode) {
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

        if (user.shouldCancelInteraction()) {
            return super.use(world, user, hand);
        } else {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }

    public static void dyeBundleInteraction(ItemStack bundleStack, DyeColorant dyeColorant) {
        List<DyeBufferEntry> currentDyeBuffers = DyeBufferEntry.readDyeBufferEntries(bundleStack);

        boolean doesBufferExist = false;

        if (!currentDyeBuffers.isEmpty()) {
            for (int i = 0; i < currentDyeBuffers.size(); i++) {
                DyeBufferEntry bufferEntry = currentDyeBuffers.get(i);

                if (bufferEntry.getDyeColorant() == dyeColorant) {
                    if (!bufferEntry.decrementBufferSize()) {
                        currentDyeBuffers.remove(i);

                        decrementFirstStack(bundleStack);
                    }

                    doesBufferExist = true;

                    break;
                }
            }
        }

        if (!doesBufferExist) {
            DyeBufferEntry bufferEntry = new DyeBufferEntry(dyeColorant, 7);

            currentDyeBuffers.add(bufferEntry);
        }

        DyeBufferEntry.writeDyeBufferEntries(currentDyeBuffers, bundleStack);
    }

    //----------------------------------------------------------------------------------------------------

    public ItemStack getFirstStack(ItemStack stack) {
        NbtCompound bundleNbt = stack.getOrCreateNbt();
        if (!bundleNbt.contains("Items")) {
            return null;
        } else {
            NbtList bundleItemsList = bundleNbt.getList("Items", 10);
            if (bundleItemsList.isEmpty()) {
                return null;
            } else {
                NbtCompound itemNbt = bundleItemsList.getCompound(0);
                return ItemStack.fromNbt(itemNbt);
            }
        }
    }

    private static boolean decrementFirstStack(ItemStack bundleStack) {
        NbtCompound bundleNbt = bundleStack.getOrCreateNbt();
        if (!bundleNbt.contains("Items")) {
            return false;
        } else {
            NbtList bundleItemsList = bundleNbt.getList("Items", 10);
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

                bundleNbt.put("Items", bundleItemsList);

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
        return (otherStack.isEmpty() || otherStack.getItem() instanceof DyeItem)
                && super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    private static void attemptShuffleItemsPacket(World world) {
        if (world.isClient && DyeBundleScreenEvent.getCachedVerticalScroll() != 0F) {
            //System.out.println("Sending update packet for bundle to the server");
            Jello.CHANNEL.clientHandle().send(new DyeBundlePackets.ScreenScrollPacket(DyeBundleScreenEvent.getCachedVerticalScroll()));

            DyeBundleScreenEvent.resetCachedVerticalScroll();
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient && entity instanceof PlayerEntity player) {
            if (tooltipTickCounter != 0) {
                ItemStack possibleBundle = player.getMainHandStack();

                if (possibleBundle.getItem() instanceof DyeBundleItem) {
                    tooltipTickCounter--;
                } else {
                    tooltipTickCounter -= 2;
                }
            }

            attemptShuffleItemsPacket(world);
        }
    }

    public void startTooltipTimer() {
        tooltipTickCounter = 100;
    }

    public static int getTooltipTime() {
        return tooltipTickCounter;
    }
}
