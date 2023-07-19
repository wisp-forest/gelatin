package io.wispforest.gelatin.dye_entries.data.recipe;

import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_entries.variants.impl.VanillaItemVariants;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.data.GelatinTags;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DyeBlockVariantRecipe extends SpecialCraftingRecipe {

    private int stackReturnCount = 0;
    private DyeableBlockVariant variant = null;
    private DyeColorant dyeColorant;

    public DyeBlockVariantRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean alreadyHasDye = false;
        boolean hasBlockVariant = false;

        int stackReturnCount = 0;
        DyeableBlockVariant variant = null;
        DyeColorant dyeColorant = null;

        for(int width = 0; width < inventory.getWidth(); width++){
            for(int height = 0; height < inventory.getHeight(); height++){
                ItemStack stack = inventory.getStack(width + height * inventory.getWidth());

                if (stack.isIn(GelatinTags.Items.VANILLA_DYE) || stack.isIn(VanillaItemVariants.DYE.getPrimaryTag())) {
                    if(alreadyHasDye || !stack.getItem().isDyed()) {
                        return false;
                    }

                    dyeColorant = stack.getItem().getDyeColorant();

                    alreadyHasDye = true;
                } else if(stack.getItem() instanceof BlockItem){ //stack.isIn(JelloTags.Items.ALL_COLORED_VARIANTS)
                    if(variant == null) {
                        DyeableBlockVariant possibleVariant = DyeableBlockVariant.getVariantFromBlock(stack.getItem());

                        if (possibleVariant != null) {
                            stackReturnCount = 1;
                            variant = possibleVariant;

                            hasBlockVariant = true;
                        }
                    } else {
                        stackReturnCount++;
                        if(variant.isSuchAVariant(stack.getItem(), true)){
                            hasBlockVariant = true;
                        } else {
                            return false;
                        }
                    }
                } else if(!stack.isEmpty()){
                    return false;
                }
            }
        }

        if(hasBlockVariant && alreadyHasDye){
            this.stackReturnCount = stackReturnCount;
            this.variant = variant;
            this.dyeColorant = dyeColorant;

            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        return new ItemStack(this.variant.getColoredBlockItem(this.dyeColorant), this.stackReturnCount);
    }

    @Override
    public boolean fits(int width, int height) {
        return (width >= 2 && height >= 1) || (width >= 1 && height >= 2);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GelatinRecipeSerializers.DYE_BLOCK_VARIANT;
    }
}
