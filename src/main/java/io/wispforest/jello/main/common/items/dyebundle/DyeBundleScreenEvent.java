package io.wispforest.jello.main.common.items.dyebundle;

import io.wispforest.jello.api.events.HotbarMouseEvents;
import io.wispforest.jello.api.mixin.mixins.client.accessors.HandledScreenAccessor;
import io.wispforest.jello.main.common.Jello;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.Collections;

public class DyeBundleScreenEvent implements ScreenMouseEvents.AllowMouseScroll, HotbarMouseEvents.AllowMouseScroll {

    private static double cachedVerticalScrollAmount = 0;

    @Override
    public boolean allowMouseScroll(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(screen instanceof HandledScreen handledScreen) {
            Slot focusedSlot = ((HandledScreenAccessor)handledScreen).getFocusedSlot();

            if(focusedSlot != null) {
                ItemStack possibleBundle = focusedSlot.hasStack() ? focusedSlot.getStack() : Items.AIR.getDefaultStack();

                if (handledScreen.getScreenHandler().getCursorStack().isEmpty() && focusedSlot.getStack().getItem() instanceof DyeBundle) {

                    cachedVerticalScrollAmount += verticalAmount;

                    DyeBundlePackets.ScreenScrollPacket.scrollThruBundle(possibleBundle, verticalAmount);

                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean allowMouseScroll(ClientPlayerEntity player, double horizontalAmount, double verticalAmount) {
        ItemStack possibleBundle = player.getMainHandStack();

        if(possibleBundle.getItem() instanceof DyeBundle dyeBundle && player.shouldCancelInteraction()){
            dyeBundle.startTooltipTimer();

            cachedVerticalScrollAmount += verticalAmount;

            DyeBundlePackets.ScreenScrollPacket.scrollThruBundle(possibleBundle, verticalAmount);

            return false;
        }

        return true;
    }

    public static void resetCachedVerticalScroll(){
        cachedVerticalScrollAmount = 0F;
    }

    public static double getCachedVerticalScroll(){
        return cachedVerticalScrollAmount;
    }
}
