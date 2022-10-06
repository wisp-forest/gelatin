package io.wispforest.gelatin.dye_entries.item;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
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
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Random;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class GelatinDyeItem extends DyeItem implements DyeItemStorage, ItemColorProvider {

    public static final String TEXTURE_VARIANT_KEY = "Texture_variant";
    public static final int NUMBER_OF_TEXTURE_VAR = 9;

    protected int textureVariant;

    public GelatinDyeItem(DyeColorant mainColor, Settings settings) {
        super(DyeColorantRegistry.Constants.NULL_VALUE_OLD, settings);

        this.setDyeColor(mainColor);

        char[] chracters = mainColor.getName().toCharArray();

        Random rand = new Random(Character.getNumericValue(chracters[0]) + Character.getNumericValue(chracters[chracters.length - 1]));

        this.textureVariant = rand.nextInt(NUMBER_OF_TEXTURE_VAR);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return this.getDyeColorant().getBaseColor();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment()) {
            float[] HSL = ColorUtil.rgbToHsl(((GelatinDyeItem) user.getMainHandStack().getItem()).getDyeColorant().getBaseColor());

            user.sendMessage(Text.of(String.format("HSL: { %f, %f, %f}", HSL[0], HSL[1], HSL[2])), true);
        }

        return super.use(world, user, hand);
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        super.postProcessNbt(nbt);

        nbt.remove(TEXTURE_VARIANT_KEY);
    }

    public static float getTextureVariant(ItemStack itemStack) {
        float textureVar = ((GelatinDyeItem)itemStack.getItem()).textureVariant;

        return textureVar / (NUMBER_OF_TEXTURE_VAR - 1);
    }

    public static Comparator<ItemStack> dyeStackHslComparator(int component) {
        return Comparator.comparingDouble(stack -> ColorUtil.rgbToHsl(((GelatinDyeItem) stack.getItem()).getDyeColorant().getBaseColor())[component]);
    }

    @Override
    public boolean isDyeItem() {
        return true;
    }
}
