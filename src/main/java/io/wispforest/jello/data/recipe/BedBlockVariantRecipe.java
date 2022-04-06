package io.wispforest.jello.data.recipe;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.data.tags.JelloTags;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BedBlockVariantRecipe extends SpecialCraftingRecipe {

    private DyeColorant dyeColorant = null;

    private static final TagKey<Item> WOOL_BLOCK_TAG = VanillaBlockVariants.WOOL.primaryItemTag;

    public BedBlockVariantRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        boolean woolTop = false;
        boolean woolMiddle = false;

        DyeableBlockVariant variant = null;
        DyeColorant dyeColorant = null;

        for(int width = 0; width < inventory.getWidth(); width++){
            for(int height = 0; height < inventory.getHeight(); height++) {
                int craftingInvIndex = width + height * inventory.getWidth();
                ItemStack stack = inventory.getStack(craftingInvIndex);

                if (craftingInvIndex < 3) {
                    if(woolTop){
                        if(!stack.isIn(WOOL_BLOCK_TAG) || dyeColorant != variant.getColorFromEntry((BlockItem) stack.getItem())){
                            return false;
                        }
                    }
                    else if (stack.isIn(WOOL_BLOCK_TAG)) {
                        woolTop = true;

                        variant = DyeableBlockVariant.getVariantFromBlock(((BlockItem) stack.getItem()));

                        if (variant == null || !stack.isIn(JelloTags.Items.ALL_COLORED_VARIANTS)) {
                            return false;
                        }

                        dyeColorant = variant.getColorFromEntry((BlockItem) stack.getItem());
                    }
                } else if (craftingInvIndex < 6) {
                    if(woolTop){
                        if(!stack.isIn(ItemTags.PLANKS) || woolMiddle){
                            return false;
                        }
                    } else if(woolMiddle){
                        if(!stack.isIn(WOOL_BLOCK_TAG) || dyeColorant != variant.getColorFromEntry((BlockItem) stack.getItem())){
                            return false;
                        }
                    } else if (stack.isIn(WOOL_BLOCK_TAG)) {
                        woolMiddle = true;

                        variant = DyeableBlockVariant.getVariantFromBlock(((BlockItem) stack.getItem()));

                        if (variant == null || !stack.isIn(JelloTags.Items.ALL_COLORED_VARIANTS)) {
                            return false;
                        }

                        dyeColorant = variant.getColorFromEntry((BlockItem) stack.getItem());
                    }
                }else{
                    if(woolMiddle){
                        if(!stack.isIn(ItemTags.PLANKS) || woolTop){
                            return false;
                        }
                    }
                }
            }
        }

        if(woolTop || woolMiddle){
            this.dyeColorant = dyeColorant;
            return true;
        }else{
            return false;
        }
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        return new ItemStack(VanillaBlockVariants.BED.getColoredBlock(dyeColorant),1);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JelloRecipeSerializers.DYE_BLOCK_VARIANT;
    }
}
