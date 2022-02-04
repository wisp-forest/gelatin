package com.dragon.jello.items;

import com.dragon.jello.blocks.SlimeBlockColored;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;

public class SlimeBlockMultiColorItem extends BlockItem implements ItemColorProvider {
    public static final String NBT_COLOR_KEY = "SlimeColor";

    private final DyeColor dyeColor;
    private int blockColor;

    public SlimeBlockMultiColorItem(SlimeBlockColored block, Settings settings) {
        super(block, settings);

        this.dyeColor = block.getDyeColor();
        this.blockColor = block.getBlockColor();
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        super.postProcessNbt(nbt);
        nbt.putInt(NBT_COLOR_KEY, blockColor);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return blockColor;

//        if(stack.getNbt() != null && stack.getNbt().contains(NBT_COLOR_KEY)){
//            return stack.getNbt().getInt(NBT_COLOR_KEY);
//        }else{
//            return 0xFFFFFF;
//        }
    }
}
