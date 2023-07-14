package io.wispforest.jello.data.recipe;

import io.wispforest.jello.item.GelatinSolutionItem;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.jellocup.JelloCupCreationHandler;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class JelloCupRecipe extends SpecialCraftingRecipe {

    public JelloCupRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean hasGelatinSolution = false;
        int sugarCupAmount = 0;

        for(int i = 0; i < inventory.getWidth(); ++i) {
            for(int j = 0; j < inventory.getHeight(); ++j) {
                ItemStack itemStack = inventory.getStack(i + j * inventory.getWidth());

                var item = itemStack.getItem();

                if(item == JelloItems.GELATIN_SOLUTION){
                    JelloCupCreationHandler data = JelloCupCreationHandler.getData(itemStack);

                    if(data == null || !data.validJelloCupCrafting(itemStack) || hasGelatinSolution) return false;

                    hasGelatinSolution = true;

                    break;
                } else if(item == JelloItems.JelloCups.SUGAR_CUP){
                    sugarCupAmount += 1;

                    if(sugarCupAmount > 3) return false;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack gelatinSolution = ItemStack.EMPTY;
        JelloCupCreationHandler data = null;

        for(int i = 0; i < inventory.getWidth(); ++i) {
            for(int j = 0; j < inventory.getHeight(); ++j) {
                ItemStack itemStack = inventory.getStack(i + j * inventory.getWidth());

                if(!(itemStack.getItem() instanceof GelatinSolutionItem)) continue;

                gelatinSolution = itemStack;
                data = JelloCupCreationHandler.getData(gelatinSolution);

                break;
            }
        }

        if(data == null) return ItemStack.EMPTY;

        return data.buildJelloCup(gelatinSolution);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JelloRecipeSerializers.JELLO_CUP;
    }
}
