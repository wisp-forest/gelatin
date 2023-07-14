package io.wispforest.jello.misc.itemgroup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class JelloItemSettings extends Item.Settings {

    public ItemGroup group = null;
    public ItemGroup.StackVisibility visibility = ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS;

    public JelloItemSettings group(ItemGroup group){
        this.group = group;

        return this;
    }

    public JelloItemSettings group(ItemGroup group, ItemGroup.StackVisibility visibility){
        this.group = group;
        this.visibility = visibility;

        return this;
    }
}
