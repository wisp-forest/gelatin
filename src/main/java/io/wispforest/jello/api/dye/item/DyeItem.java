package io.wispforest.jello.api.dye.item;

import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.mixin.ducks.DyeItemStorage;
import io.wispforest.jello.api.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class DyeItem extends net.minecraft.item.DyeItem implements DyeItemStorage, ItemColorProvider {

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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        float[] HSL = ColorUtil.getHSLfromColor(((DyeItem)user.getMainHandStack().getItem()).getDyeColor().getBaseColor());

        user.sendMessage(Text.of(String.format("HSL: { %f, %f, %f}", HSL[0], HSL[1], HSL[2])), true);

        return super.use(world, user, hand);
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
