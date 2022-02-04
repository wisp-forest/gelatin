package com.dragon.jello.items;

import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.awt.*;

public class SlimeBallColored extends Item implements ItemColorProvider {

    private int itemColor;
    private final DyeColor dyeColor;

    public SlimeBallColored(DyeColor dyeColor, Settings settings) {
        super(settings);

        float[] colorComp = dyeColor.getColorComponents();

        this.itemColor = new Color(colorComp[0], colorComp[1], colorComp[2], 1.0F).getRGB();
        this.dyeColor = dyeColor;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return itemColor;
    }
}
