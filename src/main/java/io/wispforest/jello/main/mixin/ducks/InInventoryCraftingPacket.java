package io.wispforest.jello.main.mixin.ducks;

import io.wispforest.owo.network.ServerAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public interface InInventoryCraftingPacket {

    record CraftPacket(int itemSlotID1, ItemStack currentCursorStack, Identifier shapelessRecipeID){

        private static final Logger LOGGER_INV = LogManager.getLogger("InInventoryTest");

        public static void craftFromStack(CraftPacket packet, ServerAccess access){
            ServerPlayerEntity player = access.player();

            NbtList debugList = new NbtList();
            LOGGER_INV.info("[ServerSide] B:" + player.getInventory().writeNbt(debugList).toString());

            ItemStack itemStack1 = player.getInventory().getStack(packet.itemSlotID1());
            ItemStack itemStack2 = packet.currentCursorStack; //((ScreenHandlerAccessor)player.currentScreenHandler).getPreviousCursorStack(); //player.getInventory().getStack(packet.itemSlotID2());

            LOGGER_INV.info("[ServerSide] S:" + itemStack1.toString() + " / " + itemStack2.toString());

            CraftingInventory craftingInventory = new CraftingInventory(new ScreenHandler(null, -1) {
                @Override
                public boolean canUse(PlayerEntity player) {
                    return false;
                }
            }, 2, 1);

            craftingInventory.setStack(0, itemStack1);
            craftingInventory.setStack(1, itemStack2);

            Optional<ShapelessRecipe> recipe = (Optional<ShapelessRecipe>) access.runtime().getRecipeManager().get(packet.shapelessRecipeID());

            if(recipe.isPresent()){
                DefaultedList<ItemStack> remainderStacks = recipe.get().getRemainder(craftingInventory);

                ItemStack craftedStack = recipe.get().craft(craftingInventory);

                for(int i = 0; i < remainderStacks.size(); ++i) {
                    ItemStack itemStack = craftingInventory.getStack(i);
                    ItemStack itemStackTest = remainderStacks.get(i);
                    if (!itemStack.isEmpty()) {
                        craftingInventory.removeStack(i, 1);
                        itemStack = craftingInventory.getStack(i);
                    }

                    if (!itemStackTest.isEmpty()) {
                        if (itemStack.isEmpty()) {
                            if(i == 0){
                                player.currentScreenHandler.setStackInSlot(packet.itemSlotID1(), player.currentScreenHandler.getRevision() + 1 & 32767, itemStackTest);
                            }
                            else{
                                player.currentScreenHandler.setCursorStack(itemStackTest);
                                player.currentScreenHandler.setPreviousCursorStack(itemStackTest);
                            }
//                            craftingInventory.setStack(i, itemStackTest);
                        } else if (ItemStack.areItemsEqualIgnoreDamage(itemStack, itemStackTest) && ItemStack.areNbtEqual(itemStack, itemStackTest)) {
                            itemStackTest.increment(itemStack.getCount());
                            if(i == 0){
                                player.currentScreenHandler.setStackInSlot(packet.itemSlotID1(), player.currentScreenHandler.getRevision(), itemStackTest);
                            }
                            else{
                                player.currentScreenHandler.setCursorStack(itemStackTest);
                                player.currentScreenHandler.setPreviousCursorStack(itemStackTest);
                            }
//                            craftingInventory.setStack(i, itemStackTest);
                        } else if (!player.getInventory().insertStack(itemStackTest)) {
                            player.dropItem(itemStackTest, false);
                        }
                    }
                }

                LOGGER_INV.info("[ServerSide] R:" + remainderStacks.toString());

//                itemStack1.decrement(1);
//                itemStack2.decrement(1);
//
//                player.currentScreenHandler.setStackInSlot(packet.itemSlotID1(), player.currentScreenHandler.getRevision(), itemStack1);

                //LOGGER_INV.info("[ServerSide] T:" + player.currentScreenHandler.getCursorStack().toString());

                player.currentScreenHandler.setCursorStack(itemStack2);
                player.currentScreenHandler.setPreviousCursorStack(itemStack2);

//                for(ItemStack stack : remainderStacks){
//                    if(stack.getItem() != Items.AIR) {
//                        player.giveItemStack(stack);
//                    }
//                }
//
                player.giveItemStack(craftedStack);

                player.currentScreenHandler.sendContentUpdates();
                player.currentScreenHandler.syncState();
                //player.currentScreenHandler.updateToClient();

                debugList = new NbtList();
                LOGGER_INV.info("[ServerSide] A:" + player.getInventory().writeNbt(debugList).toString());
                LOGGER_INV.info("----------------------------------------------------------------------");
            }
        }
    }
}
