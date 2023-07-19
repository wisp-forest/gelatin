package io.wispforest.gelatin.dye_entries.item;

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

        this.setDyeColor(block.getDyeColorant());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return this.getDyeColorant().getBaseColor();
    }
}
