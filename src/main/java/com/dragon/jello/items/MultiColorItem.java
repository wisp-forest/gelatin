package com.dragon.jello.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.awt.*;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class MultiColorItem extends Item implements ItemColorProvider {

    private int itemColor;
    private final DyeColor dyeColor;

    public MultiColorItem(DyeColor dyeColor, Settings settings) {
        super(settings);

        float[] colorComp = dyeColor.getColorComponents();

        this.itemColor = new Color(colorComp[0], colorComp[1], colorComp[2], 1.0F).getRGB();
        this.dyeColor = dyeColor;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return itemColor;
    }
}
