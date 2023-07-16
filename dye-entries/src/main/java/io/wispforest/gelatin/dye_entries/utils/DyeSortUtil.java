package io.wispforest.gelatin.dye_entries.utils;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class DyeSortUtil {

    public static void sortColoredStacks(List<ItemStack> dyeStacks, Function<ItemStack, DyeColorant> colorantFunc){
        dyeStacks.sort(dyeStackHslComparator(2, colorantFunc));
        dyeStacks.sort(dyeStackHslComparator(1, colorantFunc));
        dyeStacks.sort(dyeStackHslComparator(0, colorantFunc));
    }

    public static Comparator<ItemStack> dyeStackHslComparator(int component, Function<ItemStack, DyeColorant> colorantFunc) {
        return Comparator.comparingDouble(stack -> ColorUtil.rgbToHsl(colorantFunc.apply(stack).getBaseColor())[component]);
    }
}
