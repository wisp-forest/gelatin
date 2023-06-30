package io.wispforest.jello.client.gui.components;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class ItemStackBasedInventory implements ImplementedInventory {

    private final DefaultedList<ItemStack> inventory;
    private final ItemStack stack;

    protected ItemStackBasedInventory(ItemStack stack){
        NbtList nbtElements = stack.getOrCreateNbt().getList("Items", NbtList.COMPOUND_TYPE);

        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(nbtElements.size(), ItemStack.EMPTY);

        for(int i = 0; i < nbtElements.size(); i++){
            inventory.set(i, ItemStack.fromNbt((NbtCompound) nbtElements.get(i)));
        }

        this.inventory = inventory;
        this.stack = stack;
    }

    public static Inventory of(ItemStack stack){
        return of(stack, 0);
    }

    public static Inventory of(ItemStack stack, int emptyInvSize){
        if(stack.hasNbt() && stack.getNbt().contains("Items")){
            NbtList list = stack.getNbt().getList("Items", NbtElement.COMPOUND_TYPE);

            if(!list.isEmpty()) return new ItemStackBasedInventory(stack);
        }

        return new SimpleInventory(emptyInvSize);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public ItemStack getStack(){
        return stack;
    }
}
