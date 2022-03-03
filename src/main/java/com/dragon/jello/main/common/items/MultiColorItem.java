package com.dragon.jello.main.common.items;

import com.dragon.jello.api.dye.DyeColorant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.function.IntPredicate;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class MultiColorItem extends Item implements ItemColorProvider {

    private final IntPredicate tintAbove;
    private int itemColor;
    private final DyeColorant dyeColor;

    public MultiColorItem(DyeColorant dyeColor, Settings settings, IntPredicate tintAbove) {
        super(settings);
        this.tintAbove = tintAbove;

        float[] colorComp = dyeColor.getColorComponents();

        this.itemColor = new Color(colorComp[0], colorComp[1], colorComp[2], 1.0F).getRGB();
        this.dyeColor = dyeColor;
    }

    public MultiColorItem(DyeColorant dyeColor, Settings settings) {
        this(dyeColor, settings, (number) -> true);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        if(tintAbove.test(tintIndex)){
            return itemColor;
        }else{
            return -1;
        }
    }
}
