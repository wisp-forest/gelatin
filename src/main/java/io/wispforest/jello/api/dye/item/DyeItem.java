package io.wispforest.jello.api.dye.item;

import io.wispforest.jello.api.JelloAPI;
import io.wispforest.jello.api.dye.RandomDyeColorStuff;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.mixin.ducks.DyeRedirect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class DyeItem extends net.minecraft.item.DyeItem implements DyeRedirect, ItemColorProvider {

    public static final String TEXTURE_VARIANT_KEY = "Texture_variant";
    private static final int NUMBER_OF_TEXTURE_VAR = 9;

    protected final DyeColorant mainColor;

    protected int texture_varaint = 0;

    public DyeItem(DyeColorant mainColor, Settings settings) {
        super(DyeColorRegistry.NULL_VALUE_OLD, settings);

        this.mainColor = mainColor;

        if(mainColor != null){
            DyeColorRegistry.DYE_COLOR_TO_DYEITEM.put(this.mainColor, this);

            char[] chracters = mainColor.getName().toCharArray();

            Random rand = new Random(Character.getNumericValue(chracters[0]) + Character.getNumericValue(chracters[chracters.length - 1]));

            this.texture_varaint = rand.nextInt(NUMBER_OF_TEXTURE_VAR);
        }
    }

    @Override
    public Text getName() {
        return new LiteralText(mainColor.getDisplayName() + " Dye");
    }

    @Override
    public Text getName(ItemStack stack) {
        return getName();
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return this.getDyeColor().getBaseColor();
    }

    @Override
    public DyeColorant getDyeColor() {
        return this.mainColor;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        Identifier itemId = Registry.ITEM.getId(this);

        if (Objects.equals(itemId.getNamespace(), "jello_dji")) {
            if (this.isIn(group) ){//&& group == ItemGroup.MISC) {
                if (stacks.isEmpty()) {
                    stacks.add(new ItemStack(this));
                } else {
                    for (int i = 0; i < stacks.size(); i++) {
                        if(stacks.get(i).getItem() instanceof DyeItem dyeItem) {
                            DyeColorant listEntryColor = dyeItem.getDyeColor();

                            if (this.getDyeColor().getBaseColor() > listEntryColor.getBaseColor()) {
                                if (i + 1 < stacks.size()) {
                                    stacks.add(i, new ItemStack(this));
                                } else {
                                    stacks.add(new ItemStack(this));
                                }
                                return;
                            }
                        }
                    }

                    stacks.add(0, new ItemStack(this));
                }
            }
        }else{
            super.appendStacks(group, stacks);
        }
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        super.postProcessNbt(nbt);
        this.setTextureVariant(nbt);
    }

    private void setTextureVariant(NbtCompound nbt){
        nbt.putInt(TEXTURE_VARIANT_KEY, texture_varaint);
    }

    private static int getTextureValue(ItemStack stack){
        return stack.getOrCreateNbt().getInt(TEXTURE_VARIANT_KEY);
    }

    public static float getTextureVariant(ItemStack itemStack){
        float textureVar = getTextureValue(itemStack);

        return textureVar / (NUMBER_OF_TEXTURE_VAR - 1);
    }
}
