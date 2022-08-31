package io.wispforest.dye_entries.item;

import io.wispforest.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.dye_registry.ducks.DyeItemStorage;
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

        ((DyeItemStorage)this).setDyeColor(((DyeBlockStorage) block).getDyeColorant());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return ((DyeItemStorage)this).getDyeColorant().getBaseColor();
    }
}
