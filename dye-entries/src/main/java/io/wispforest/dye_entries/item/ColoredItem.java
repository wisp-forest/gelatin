package io.wispforest.dye_entries.item;

import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.ducks.DyeItemStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.IntPredicate;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class ColoredItem extends Item implements ItemColorProvider {

    private final IntPredicate tintAbove;

    public ColoredItem(DyeColorant dyeColor, Settings settings, IntPredicate tintAbove) {
        super(settings);

        this.tintAbove = tintAbove;

        ((DyeItemStorage)this).setDyeColor(dyeColor);
    }

    public ColoredItem(DyeColorant dyeColor, Settings settings) {
        this(dyeColor, settings, (number) -> true);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return tintAbove.test(tintIndex) ? ((DyeItemStorage)this).getDyeColorant().getBaseColor() : -1;
    }
}
