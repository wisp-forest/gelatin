package com.dragon.jello.dyelib;

import com.dragon.jello.dyelib.mixin.ducks.DyeRedirect;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Locale;

public class DyenamicDyeItem extends DyeItem implements ItemColorProvider, DyeRedirect {
    private final DyeColorRegistry.DyeColor mainColor;

    private DyeColorRegistry.DyeColor dynamicColor;
    private final boolean isDynamicColor;

    public static final String COLOR_KEY = "color";

    public DyenamicDyeItem(DyeColorRegistry.DyeColor mainColor, Settings settings) {
        super(DyeColor.BLACK, settings);

        this.mainColor = mainColor;
        dynamicColor = null;
        this.isDynamicColor = false;
    }

    public DyenamicDyeItem(Settings settings) {
        super(DyeColor.BLACK, settings);

        this.mainColor = null;
        this.dynamicColor = RandomDyeColorStuff.getRandomlyRegisteredDyeColor();
        this.isDynamicColor = true;
    }

    @Override
    public Text getName() {
        if(mainColor == null && dynamicColor != null) {
            return new LiteralText(dynamicColor.getDisplayName() + " Dye");
        }else{
            return new LiteralText(mainColor.getDisplayName() + " Dye");
        }
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
        if(mainColor == null && dynamicColor != null) {
            setColor(nbt, dynamicColor.getId().toString());
        }else{
            setColor(nbt, "null");
        }

        super.postProcessNbt(nbt);
    }

    private static void setColor(NbtCompound nbt, String colorID){
        nbt.putString(COLOR_KEY, colorID);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if(mainColor == null && dynamicColor != null) {
            return dynamicColor.baseColor();
        }else{
            return mainColor.baseColor();
        }
    }

    @Override
    public DyeColorRegistry.DyeColor getDyeColor() {
        if(mainColor == null && dynamicColor != null) {
            return dynamicColor;
        }else{
            return mainColor;
        }
    }
}
