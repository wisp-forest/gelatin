package io.wispforest.jello.item;

import io.wispforest.jello.api.dye.DyeColorant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.IntPredicate;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class MultiColorItem extends Item implements ItemColorProvider {

    private final DyeColorant dyeColor;
    private final IntPredicate tintAbove;

    public MultiColorItem(DyeColorant dyeColor, Settings settings, IntPredicate tintAbove) {
        super(settings);

        this.dyeColor = dyeColor;
        this.tintAbove = tintAbove;
    }

    public MultiColorItem(DyeColorant dyeColor, Settings settings) {
        this(dyeColor, settings, (number) -> true);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return tintAbove.test(tintIndex) ? dyeColor.getBaseColor() : -1;
    }
}
