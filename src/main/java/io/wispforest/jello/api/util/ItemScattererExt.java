package io.wispforest.jello.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScattererExt {

    public static void spawn(World world, BlockPos blockPos, ItemStack itemStack){
        ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
    }
}
