package io.wispforest.jello.item;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

import java.util.Set;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class ArtistPaletteItem extends Item implements ItemColorProvider {

    private static final String PALETTE_KEY = "PaletteOrder";
    private static final String COLORED_KEY = "Colored";
    private static final String TIMES_USED_KEY = "TimesUsed";

    public static final int MAX_USES = 256;

    public static final Set<DyeColorant> ALLOWED_COLORS = Set.of(DyeColorantRegistry.RED,
            DyeColorantRegistry.GREEN,
            DyeColorantRegistry.BLUE,
            DyeColorantRegistry.WHITE,
            DyeColorantRegistry.BLACK);

    public ArtistPaletteItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(getDefaultStack());
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.getOrCreateNbt().getInt(TIMES_USED_KEY) > 0;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.hsvToRgb((MAX_USES - stack.getOrCreateNbt().getInt(TIMES_USED_KEY)) / (float) MAX_USES, 1, 1);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round(13.0F - (float) stack.getOrCreateNbt().getInt(TIMES_USED_KEY) * 13.0F / MAX_USES);
    }

    public static ItemStack use(ItemStack palette) {
        var nbt = palette.getOrCreateNbt();

        final var timesUsed = nbt.getInt(TIMES_USED_KEY) + 1;
        nbt.putInt(TIMES_USED_KEY, timesUsed);

        return timesUsed >= MAX_USES ? JelloItems.EMPTY_ARTIST_PALETTE.getDefaultStack() : palette;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        setStackColors(stack.getOrCreateNbt(), ALLOWED_COLORS.toArray(DyeColorant[]::new));
        return stack;
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        super.onItemEntityDestroyed(entity);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            return -1;
        }
        NbtCompound nbt = stack.getOrCreateNbt();

        if (nbt.getBoolean(COLORED_KEY)) {
            NbtList nbtList = nbt.getList(PALETTE_KEY, NbtList.STRING_TYPE);

            return DyeColorantRegistry.DYE_COLOR.get(Identifier.tryParse(nbtList.getString(tintIndex - 1))).getBaseColor();
        } else {
            return -1;
        }
    }

    public static void setStackColors(NbtCompound nbt, DyeColorant... dyeColorants) {
        if (dyeColorants.length != 5) {
            nbt.putBoolean(COLORED_KEY, false);
            return;
        }

        NbtList nbtList = new NbtList();

        for (DyeColorant dyeColorant : dyeColorants) {
            nbtList.add(NbtString.of(dyeColorant.toString()));
        }

        nbt.putBoolean(COLORED_KEY, true);
        nbt.put(PALETTE_KEY, nbtList);
    }
}
