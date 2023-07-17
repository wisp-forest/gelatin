package io.wispforest.jello.misc.itemgroup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;

public class JelloItemSettings extends Item.Settings {

    public RegistryKey<ItemGroup> group = null;
    public ItemGroup.StackVisibility visibility = ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS;

    public JelloItemSettings group(RegistryKey<ItemGroup> group){
        this.group = group;

        return this;
    }

    public JelloItemSettings group(RegistryKey<ItemGroup> group, ItemGroup.StackVisibility visibility){
        this.group = group;
        this.visibility = visibility;

        return this;
    }
}
