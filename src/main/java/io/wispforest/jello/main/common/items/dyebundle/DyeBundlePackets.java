package io.wispforest.jello.main.common.items.dyebundle;

import io.wispforest.owo.network.ServerAccess;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

import java.util.Collections;

public class DyeBundlePackets {

    public record ScreenScrollPacket(double verticalAmount){

        public static void scrollBundle(ScreenScrollPacket packet, ServerAccess access){
            scrollThruBundle(access.player().getMainHandStack(), packet.verticalAmount());
        }

        public static void scrollThruBundle(ItemStack possibleBundle, double verticalAmount){
            DefaultedList<ItemStack> defaultedList = DefaultedList.of();
            BundleItem.getBundledStacks(possibleBundle).forEach(defaultedList::add);

            //System.out.println("Vertical: " + verticalAmount);

            Collections.rotate(defaultedList, (int) verticalAmount);

            NbtCompound nbtCompoundBundle = possibleBundle.getOrCreateNbt();

            NbtList nbtList = new NbtList();

            for (ItemStack stack : defaultedList) {
                NbtCompound nbtCompoundStack = new NbtCompound();

                stack.writeNbt(nbtCompoundStack);
                nbtList.add(nbtCompoundStack);
            }

            nbtCompoundBundle.put("Items", nbtList);

        }
    }
}
