package io.wispforest.jello.api.dye.item.group;

import io.wispforest.jello.main.common.data.tags.JelloTags;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class MiscItemGroup extends OwoItemGroup {

    public MiscItemGroup() {
        super(6, "misc");
    }

    @Override
    protected void setup() {
        this.addTab(Icon.of(Items.LAVA_BUCKET), "misc", null, ItemGroupTab.DEFAULT_TEXTURE);
        this.addTab(Icon.of(ItemRegistry.MainItemRegistry.DYNAMIC_DYE), "dyes", JelloTags.Items.DYE_ITEMS, ItemGroupTab.DEFAULT_TEXTURE);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.LAVA_BUCKET);
    }
}
