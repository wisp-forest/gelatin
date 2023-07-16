package io.wispforest.jello.misc.itemgroup;

import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JelloItemGroupModifier implements ItemGroupEvents.ModifyEntriesAll {

    public static JelloItemGroupModifier INSTANCE = new JelloItemGroupModifier();

    public final Map<ItemGroup, List<Pair<Item, ItemGroup.StackVisibility>>> ALL_MODIFIERS = new HashMap<>();

    @Override
    public void modifyEntries(ItemGroup group, FabricItemGroupEntries entries) {
        if(!ALL_MODIFIERS.containsKey(group)) return;

        ALL_MODIFIERS.get(group).forEach(entry -> {
            if(entry.getLeft() instanceof ItemGroupTab.ContentSupplier supplier){
                supplier.addItems(entries.getContext(), entries);
            } else {
                entries.add(entry.getLeft().getDefaultStack(), entry.getRight());
            }
        });
    }
}
