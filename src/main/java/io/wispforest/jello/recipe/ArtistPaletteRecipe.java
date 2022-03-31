package io.wispforest.jello.recipe;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.misc.ducks.DyeItemStorage;
import io.wispforest.jello.item.ArtistPalette;
import io.wispforest.jello.item.JelloItems;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ArtistPaletteRecipe extends SpecialCraftingRecipe {
    public ArtistPaletteRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        if (craftingInventory.getWidth() == 3 && craftingInventory.getHeight() == 3) {

            byte hasColorAlready = 0;

            for (int i = 0; i < craftingInventory.getWidth(); ++i) {
                for (int j = 0; j < craftingInventory.getHeight(); ++j) {
                    ItemStack itemStack = craftingInventory.getStack(i + j * craftingInventory.getWidth());

                    if (i == 1 && j == 1) {
                        if (!itemStack.isOf(JelloItems.EMPTY_ARTIST_PALETTE)) {
                            return false;
                        }
                    } else if ((j == 0) || ((i == 0 || i == 2) && j == 1)) {
                        if (itemStack.isEmpty()) {
                            return false;
                        }

                        if (itemStack.getItem() instanceof DyeItemStorage dyeItemStorage && ArtistPalette.ALLOWED_COLORS.contains(dyeItemStorage.getDyeColor())) {
                            switch (dyeItemStorage.getDyeColor().toString()) {
                                case "minecraft:red" -> {
                                    if ((hasColorAlready & 1) == 1) {
                                        return false;
                                    }
                                    hasColorAlready = (byte) (hasColorAlready | 1);
                                }
                                case "minecraft:green" -> {
                                    if ((hasColorAlready & 2) == 2) {
                                        return false;
                                    }
                                    hasColorAlready = (byte) (hasColorAlready | 2);
                                }
                                case "minecraft:blue" -> {
                                    if ((hasColorAlready & 4) == 4) {
                                        return false;
                                    }
                                    hasColorAlready = (byte) (hasColorAlready | 4);
                                }
                                case "minecraft:white" -> {
                                    if ((hasColorAlready & 8) == 8) {
                                        return false;
                                    }
                                    hasColorAlready = (byte) (hasColorAlready | 8);
                                }
                                case "minecraft:black" -> {
                                    if ((hasColorAlready & 16) == 16) {
                                        return false;
                                    }
                                    hasColorAlready = (byte) (hasColorAlready | 16);
                                }
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack itemStack = craftingInventory.getStack(1 + craftingInventory.getWidth());
        if (!itemStack.isOf(JelloItems.EMPTY_ARTIST_PALETTE)) {
            return ItemStack.EMPTY;
        } else {
            List<DyeColorant> dyeColors = new ArrayList<>();

            dyeColors.add(((DyeItemStorage) craftingInventory.getStack(3).getItem()).getDyeColor());

            for (int i = 0; i < 3; i++) {
                dyeColors.add(((DyeItemStorage) craftingInventory.getStack(i).getItem()).getDyeColor());
            }

            dyeColors.add(((DyeItemStorage) craftingInventory.getStack(5).getItem()).getDyeColor());

            ItemStack itemStack2 = new ItemStack(JelloItems.ARTIST_PALETTE, 1);

            ArtistPalette.setStackColors(itemStack2.getOrCreateNbt(), dyeColors.toArray(new DyeColorant[]{}));

            return itemStack2;
        }
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JelloRecipeSerializers.ARTIST_PALETTE;
    }
}
