package io.wispforest.jello.api.dye.item.group;

import io.wispforest.jello.api.JelloAPI;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.item.DyeItem;
import io.wispforest.jello.api.dye.registry.DyeColorantJsonTest;
import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import io.wispforest.jello.api.util.ColorUtil;
import io.wispforest.jello.main.common.data.tags.JelloTags;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.Comparator;
import java.util.Objects;

public class MiscItemGroup extends OwoItemGroup {

    public MiscItemGroup() {
        super(6, "misc");
    }

    @Override
    protected void setup() {
        this.addTab(Icon.of(Items.LAVA_BUCKET), "misc", null, ItemGroupTab.DEFAULT_TEXTURE);
        this.addTab(Icon.of(Registry.ITEM.get(new Identifier(JelloAPI.MODID, "cold_turkey_dye"))), "dyes", null, ItemGroupTab.DEFAULT_TEXTURE);
        this.addTab(Icon.of(Items.WHITE_CONCRETE), "block_vars", null, ItemGroupTab.DEFAULT_TEXTURE);
//        this.addTab(Icon.of(Items.SLIME_BALL), "item_vars", null, ItemGroupTab.DEFAULT_TEXTURE);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.LAVA_BUCKET);
    }

    @Override
    public void appendStacks(DefaultedList<ItemStack> stacks) {
        super.appendStacks(stacks);

        if(this.getSelectedTabIndex() == 1){
            stacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.getHSLfromColor(((DyeItem)stack.getItem()).getDyeColor().getBaseColor());

                return hsl[2];
            }));

            stacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.getHSLfromColor(((DyeItem)stack.getItem()).getDyeColor().getBaseColor());

                return hsl[1];
            }));

            stacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.getHSLfromColor(((DyeItem)stack.getItem()).getDyeColor().getBaseColor());

                return hsl[0];
            }));
        }else if(this.getSelectedTabIndex() == 2){
            stacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.getHSLfromColor(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColor().getBaseColor());

                return hsl[2];
            }));

            stacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.getHSLfromColor(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColor().getBaseColor());

                return hsl[1];
            }));

            stacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.getHSLfromColor(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColor().getBaseColor());

                return hsl[0];
            }));
        }
    }
}
