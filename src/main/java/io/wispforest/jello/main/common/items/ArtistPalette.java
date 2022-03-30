package io.wispforest.jello.main.common.items;

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
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class ArtistPalette extends Item implements ItemColorProvider  {
    private static final String PALETTE_KEY = "PaletteOrder";
    private static final String COLORED_KEY = "Colored";

    public static final Set<DyeColorant> ALLOWED_COLORS = Set.of(DyeColorantRegistry.RED,
            DyeColorantRegistry.GREEN,
            DyeColorantRegistry.BLUE,
            DyeColorantRegistry.WHITE,
            DyeColorantRegistry.BLACK);

    public ArtistPalette(Settings settings) {
        super(settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(getDefaultStack());
        }
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
        if(tintIndex == 0){
            return -1;
        }
        NbtCompound nbt = stack.getOrCreateNbt();

        if(nbt.getBoolean(COLORED_KEY)){
            NbtList nbtList = nbt.getList(PALETTE_KEY, NbtList.STRING_TYPE);

            return DyeColorantRegistry.DYE_COLOR.get(Identifier.tryParse(nbtList.getString(tintIndex - 1))).getBaseColor();
        }else{
            return -1;
        }
    }

    public static void setStackColors(NbtCompound nbt, DyeColorant... dyeColorants){
        if(dyeColorants.length != 5){
            nbt.putBoolean(COLORED_KEY, false);
            return;
        }

        NbtList nbtList = new NbtList();

        for(DyeColorant dyeColorant : dyeColorants){
            nbtList.add(NbtString.of(dyeColorant.toString()));
        }

        nbt.putBoolean(COLORED_KEY, true);
        nbt.put(PALETTE_KEY, nbtList);
    }
}
