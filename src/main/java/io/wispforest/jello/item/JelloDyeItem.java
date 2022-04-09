package io.wispforest.jello.item;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.ducks.DyeItemStorage;
import io.wispforest.jello.api.util.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class JelloDyeItem extends DyeItem implements DyeItemStorage, ItemColorProvider {

    public static final String TEXTURE_VARIANT_KEY = "Texture_variant";
    public static final int NUMBER_OF_TEXTURE_VAR = 9;

    protected int textureVariant = 0;

    public JelloDyeItem(DyeColorant mainColor, Settings settings) {
        super(DyeColorantRegistry.Constants.NULL_VALUE_OLD, settings);

        this.setDyeColor(mainColor);

        if (mainColor != null) {
            char[] chracters = mainColor.getName().toCharArray();

            Random rand = new Random(Character.getNumericValue(chracters[0]) + Character.getNumericValue(chracters[chracters.length - 1]));

            this.textureVariant = rand.nextInt(NUMBER_OF_TEXTURE_VAR);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return this.getDyeColorant().getBaseColor();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment()) {
            float[] HSL = ColorUtil.rgbToHsl(((JelloDyeItem) user.getMainHandStack().getItem()).getDyeColorant().getBaseColor());

            user.sendMessage(Text.of(String.format("HSL: { %f, %f, %f}", HSL[0], HSL[1], HSL[2])), true);
        }

        return super.use(world, user, hand);
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        super.postProcessNbt(nbt);

        this.setTextureVariant(nbt);
    }

    private void setTextureVariant(NbtCompound nbt) {
        nbt.putInt(TEXTURE_VARIANT_KEY, textureVariant);
    }

    private static int getTextureValue(ItemStack stack) {
        return stack.getOrCreateNbt().getInt(TEXTURE_VARIANT_KEY);
    }

    public static float getTextureVariant(ItemStack itemStack) {
        float textureVar = getTextureValue(itemStack);

        return textureVar / (NUMBER_OF_TEXTURE_VAR - 1);
    }
}
