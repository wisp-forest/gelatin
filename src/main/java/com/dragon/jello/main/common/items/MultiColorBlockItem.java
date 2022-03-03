package com.dragon.jello.main.common.items;

import com.dragon.jello.main.common.blocks.DyeableBlock;
import com.dragon.jello.api.dye.DyeColorant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class MultiColorBlockItem extends BlockItem implements ItemColorProvider {
    public static final String NBT_COLOR_KEY = "SlimeColor";

    private final DyeColorant dyeColor;
    private int blockColor;

    public MultiColorBlockItem(Block block, Settings settings) {
        super(block, settings);

        DyeableBlock dyableBlock = (DyeableBlock)block;
        this.dyeColor = dyableBlock.getDyeColor();
        this.blockColor = dyableBlock.getBlockColor();
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        super.postProcessNbt(nbt);
        nbt.putInt(NBT_COLOR_KEY, blockColor);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return blockColor;

//        if(stack.getNbt() != null && stack.getNbt().contains(NBT_COLOR_KEY)){
//            return stack.getNbt().getInt(NBT_COLOR_KEY);
//        }else{
//            return 0xFFFFFF;
//        }
    }
}