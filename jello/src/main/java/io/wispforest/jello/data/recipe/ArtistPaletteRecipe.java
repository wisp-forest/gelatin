package io.wispforest.jello.data.recipe;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.jello.item.ArtistPaletteItem;
import io.wispforest.jello.item.JelloItems;
import net.minecraft.item.DyeItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ArtistPaletteRecipe extends SpecialCraftingRecipe {
    public ArtistPaletteRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory craftingInventory, World world) {
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

                        var item = itemStack.getItem();

                        if (item instanceof DyeItem && ArtistPaletteItem.ALLOWED_COLORS.contains(item.getDyeColorant())) {
                            switch (item.getDyeColorant().toString()) {
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
    public ItemStack craft(RecipeInputInventory craftingInventory, DynamicRegistryManager registryManager) {
        ItemStack itemStack = craftingInventory.getStack(1 + craftingInventory.getWidth());
        if (!itemStack.isOf(JelloItems.EMPTY_ARTIST_PALETTE)) {
            return ItemStack.EMPTY;
        } else {
            List<DyeColorant> dyeColors = new ArrayList<>();

            dyeColors.add(craftingInventory.getStack(3).getItem().getDyeColorant());

            for (int i = 0; i < 3; i++) {
                dyeColors.add(craftingInventory.getStack(i).getItem().getDyeColorant());
            }

            dyeColors.add(craftingInventory.getStack(5).getItem().getDyeColorant());

            ItemStack itemStack2 = new ItemStack(JelloItems.ARTIST_PALETTE, 1);

            ArtistPaletteItem.setStackColors(itemStack2.getOrCreateNbt(), dyeColors.toArray(new DyeColorant[]{}));

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
