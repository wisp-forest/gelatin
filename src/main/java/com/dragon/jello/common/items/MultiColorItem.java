package com.dragon.jello.common.items;

import com.dragon.jello.lib.dyecolor.DyeColorRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.awt.*;
import java.util.function.IntPredicate;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class MultiColorItem extends Item implements ItemColorProvider {

    private final IntPredicate tintAbove;
    private int itemColor;
    private final DyeColorRegistry.DyeColor dyeColor;

    public MultiColorItem(DyeColorRegistry.DyeColor dyeColor, Settings settings, IntPredicate tintAbove) {
        super(settings);
        this.tintAbove = tintAbove;

        float[] colorComp = dyeColor.colorComponents();

        this.itemColor = new Color(colorComp[0], colorComp[1], colorComp[2], 1.0F).getRGB();
        this.dyeColor = dyeColor;
    }

    public MultiColorItem(DyeColorRegistry.DyeColor dyeColor, Settings settings) {
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
