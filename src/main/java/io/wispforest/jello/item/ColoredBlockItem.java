package io.wispforest.jello.item;

import io.wispforest.jello.api.ducks.DyeItemStorage;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.ducks.DyeBlockStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class ColoredBlockItem extends BlockItem implements ItemColorProvider {

    public ColoredBlockItem(Block block, Settings settings) {
        super(block, settings);

        ((DyeItemStorage)this).setDyeColor(((DyeBlockStorage) block).getDyeColor());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return ((DyeItemStorage)this).getDyeColorant().getBaseColor();
    }
}
