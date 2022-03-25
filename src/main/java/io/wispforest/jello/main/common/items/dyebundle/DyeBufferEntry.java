package io.wispforest.jello.main.common.items.dyebundle;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.ArrayList;
import java.util.List;

public class DyeBufferEntry {

    public static final DyeBufferEntry NULL = new DyeBufferEntry(DyeColorantRegistry.NULL_VALUE_NEW, -1);

    public static final String DYE_ENTRY_BUFFER_KEY = "BufferEntry";

    private final DyeColorant dyeColorant;
    private int bufferSize;

    public DyeBufferEntry(DyeColorant dyeColorant, int bufferSize){
        this.dyeColorant = dyeColorant;
        this.bufferSize = bufferSize;
    }

    public static void writeDyeBufferEntrys(List<DyeBufferEntry> dyeBufferEntryList, ItemStack itemStack) {
        writeDyeBufferEntrys(dyeBufferEntryList, itemStack.getOrCreateNbt());
    }

    public static void writeDyeBufferEntrys(List<DyeBufferEntry> dyeBufferEntryList, NbtCompound nbt){
        NbtList list = new NbtList();

        for(DyeBufferEntry dyeBufferEntry : dyeBufferEntryList){
            list.add(dyeBufferEntry.toNbtString());
        }

        nbt.put(DYE_ENTRY_BUFFER_KEY, list);
    }

    public static List<DyeBufferEntry> readDyeBufferEntrys(ItemStack itemStack) {
        return readDyeBufferEntrys(itemStack.getOrCreateNbt());
    }

    public static List<DyeBufferEntry> readDyeBufferEntrys(NbtCompound nbt){
        List<DyeBufferEntry> dyeBufferEntryList = new ArrayList<>();

        if(nbt.contains(DYE_ENTRY_BUFFER_KEY)) {
            NbtList list = nbt.getList(DYE_ENTRY_BUFFER_KEY, NbtList.STRING_TYPE);

            for (NbtString dyeBufferString : list.toArray(new NbtString[]{})) {
                DyeBufferEntry entry = DyeBufferEntry.readBuffer(dyeBufferString.asString());
                if (entry != NULL) {
                    dyeBufferEntryList.add(entry);
                }
            }
        }

        return dyeBufferEntryList;
    }

    private static DyeBufferEntry readBuffer(String dyeBufferString){
        String[] bufferComponents = dyeBufferString.split("/");

        DyeColorant dyeColorant;
        try {
            dyeColorant = DyeColorantRegistry.DYE_COLOR.get(Identifier.tryParse(bufferComponents[0]));
        } catch (InvalidIdentifierException e) {
            e.printStackTrace();
            return NULL;
        }

        return new DyeBufferEntry(dyeColorant, Integer.parseInt(bufferComponents[1].split(":")[1]));
    }

    @Override
    public String toString() {
        return this.dyeColorant.getId().toString() + "/" + "size:" + bufferSize;
    }

    public NbtString toNbtString(){
        return NbtString.of(this.toString());
    }

    public DyeColorant getDyeColorant() {
        return dyeColorant;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public boolean decrementBufferSize(){
        this.bufferSize -= 1;

        return bufferSize > 0;
    }

}
