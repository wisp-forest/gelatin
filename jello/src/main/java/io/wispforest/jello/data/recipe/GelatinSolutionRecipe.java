package io.wispforest.jello.data.recipe;

import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.jellocup.JelloCupCreationHandler;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GelatinSolutionRecipe extends SpecialCraftingRecipe {

    public GelatinSolutionRecipe(Identifier identifier, CraftingRecipeCategory category) {
        super(identifier, category);
    }

    public boolean matches(CraftingInventory inventory, World world) {
        Map<Item, AtomicBoolean> DEFAULT_VALUES = new HashMap<>();

        DEFAULT_VALUES.put(Items.SUGAR, new AtomicBoolean(false));
        //DEFAULT_VALUES.put(Items.POTION, new AtomicBoolean(false));
        DEFAULT_VALUES.put(Items.WATER_BUCKET, new AtomicBoolean(false));
        DEFAULT_VALUES.put(JelloItems.GELATIN, new AtomicBoolean(false));

        boolean foundCraftingStack = false;

        for(int i = 0; i < inventory.getWidth(); ++i) {
            for(int j = 0; j < inventory.getHeight(); ++j) {
                ItemStack itemStack = inventory.getStack(i + j * inventory.getWidth());

                var item = itemStack.getItem();

                for (JelloCupCreationHandler data : JelloCupCreationHandler.ALL_CUP_DATA.values()) {
                    if(data.validSolutionCrafting(itemStack)){
                        if(foundCraftingStack) return false;

                        foundCraftingStack = true;

                        break;
                    }
                }

//                if(item == Items.POTION){
//                    Potion potion = PotionUtil.getPotion(itemStack);
//
//                    if(!JelloCupItem.getValidPotions().contains(potion)) return false;
//                }

                if(DEFAULT_VALUES.containsKey(itemStack.getItem())){
                    AtomicBoolean bl = DEFAULT_VALUES.get(item);

                    if(bl.get()) return false;

                    bl.set(true);
                }
            }
        }

        for (AtomicBoolean value : DEFAULT_VALUES.values()) if(!value.get()) return false;

        return foundCraftingStack;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        JelloCupCreationHandler foundData = null;
        ItemStack ingredientStack = ItemStack.EMPTY;

        for(int i = 0; i < inventory.getWidth(); ++i) {
            for(int j = 0; j < inventory.getHeight(); ++j) {
                ItemStack itemStack = inventory.getStack(i + j * inventory.getWidth());

                if(itemStack.isEmpty()) continue;

                for (JelloCupCreationHandler data : JelloCupCreationHandler.ALL_CUP_DATA.values()) {
                    if(data.validSolutionCrafting(itemStack)){
                        foundData = data;
                        ingredientStack = itemStack;

                        break;
                    }
                }

                if(foundData == null) continue;

                break;
            }
        }

        if(foundData == null) return ItemStack.EMPTY;

        return foundData.buildSolution(ingredientStack);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JelloRecipeSerializers.GELATIN_SOLUTION;
    }
}
