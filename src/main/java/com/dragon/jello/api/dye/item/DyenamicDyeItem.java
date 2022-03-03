package com.dragon.jello.api.dye.item;

import com.dragon.jello.api.dye.DyeColorant;
import com.dragon.jello.api.dye.registry.RandomDyeColorStuff;
import com.dragon.jello.api.mixin.ducks.DyeRedirect;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DyenamicDyeItem extends DyeItem implements ItemColorProvider, DyeRedirect {
    public static final String COLOR_KEY = "color";

    private DyeColorant dynamicColor;
    private final boolean isDynamicColor;

    public DyenamicDyeItem(DyeColorant mainColor, Settings settings) {
        super(mainColor, settings);

        dynamicColor = null;
        this.isDynamicColor = false;
    }

    public DyenamicDyeItem(Settings settings) {
        super(null, settings);

        this.dynamicColor = RandomDyeColorStuff.getRandomlyRegisteredDyeColor();
        this.isDynamicColor = true;
    }

    @Override
    public Text getName() {
        return isDynamicColor ? new LiteralText("[Experimental]: " + dynamicColor.getDisplayName() + " Dye") : new LiteralText("[Experimental]: " + mainColor.getDisplayName() + " Dye");
    }

    @Override
    public Text getName(ItemStack stack) {
        return getName();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.shouldCancelInteraction()){
            ItemStack mainHandItem = user.getStackInHand(hand);

            mainHandItem.setNbt(new NbtCompound());
            this.dynamicColor = RandomDyeColorStuff.getRandomlyRegisteredDyeColor();
        }

        return super.use(world, user, hand);
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        if (isDynamicColor) {
            setColor(nbt, dynamicColor.getId().toString());
        } else {
            setColor(nbt, "null");
        }

        super.postProcessNbt(nbt);
    }

    private static void setColor(NbtCompound nbt, String colorID){
        nbt.putString(COLOR_KEY, colorID);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return this.getDyeColor().getBaseColor();
    }

    @Override
    public DyeColorant getDyeColor() {
        return isDynamicColor ? dynamicColor : super.getDyeColor();
    }
}
