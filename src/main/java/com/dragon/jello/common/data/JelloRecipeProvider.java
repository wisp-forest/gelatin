package com.dragon.jello.common.data;

import com.dragon.jello.common.Jello;
import com.dragon.jello.common.blocks.BlockRegistry;
import com.dragon.jello.common.blocks.SlimeBlockColored;
import com.dragon.jello.common.items.ItemRegistry;
import com.dragon.jello.common.tags.JelloTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipesProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;

public class JelloRecipeProvider extends FabricRecipesProvider {
    public JelloRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        for(int i = 0; i < BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.size(); i++){
            Block slab = BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.get(i);
            String slabPath = Registry.BLOCK.getId(slab).getPath();

            SlimeBlockColored block = (SlimeBlockColored) BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.get(i);
            String blockPath = Registry.BLOCK.getId(block).getPath();

            Item item = ItemRegistry.SlimeBallItemRegistry.SLIME_BALLS.get(i);
            String itemPath = Registry.ITEM.getId(item).getPath();

            createSlabRecipe(slab, Ingredient.ofItems(block))
                    .criterion("has_" + Registry.BLOCK.getId(block).getPath(), conditionsFromItem(block))
                    .offerTo(exporter);

            offerReversibleCompactingRecipes(exporter, item, block);

            ShapedRecipeJsonFactory.create(Blocks.STICKY_PISTON)
                    .input('P', Blocks.PISTON)
                    .input('S', item)
                    .pattern("S")
                    .pattern("P")
                    .criterion("has_slime_ball", conditionsFromItem(item))
                    .offerTo(exporter, new Identifier(Jello.MODID,  "sticky_piston_" + itemPath));

            ShapedRecipeJsonFactory.create(Items.LEAD, 2)
                    .input('~', Items.STRING)
                    .input('O', item)
                    .pattern("~~ ")
                    .pattern("~O ")
                    .pattern("  ~")
                    .criterion("has_slime_ball", conditionsFromItem(item))
                    .offerTo(exporter, new Identifier(Jello.MODID,  "lead_" + itemPath));

            ShapelessRecipeJsonFactory.create(Items.MAGMA_CREAM)
                    .input(Items.BLAZE_POWDER)
                    .input(item)
                    .criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
                    .offerTo(exporter, new Identifier(Jello.MODID,  "magma_cream" + itemPath));

            Item dyeItem = Registry.ITEM.get(new Identifier(block.getDyeColor().getName() + "_dye"));
            String dyePath = block.getDyeColor().getName() + "_dye";

            offerSlimeBlockDyeingRecipe(exporter, block, dyeItem, blockPath, block.getDyeColor().getName() + "_dye");
            offerSlimeBlockDyeingFullRecipe(exporter, block, dyeItem, blockPath, block.getDyeColor().getName() + "_dye");

            offerSlimeSlabDyeingRecipe(exporter, slab, dyeItem, slabPath, block.getDyeColor().getName() + "_dye");
            offerSlimeSlabDyeingFullRecipe(exporter, slab, dyeItem, slabPath, block.getDyeColor().getName() + "_dye");

            offerSlimeBallDyeingRecipe(exporter, item, dyeItem, itemPath, dyePath);
        }
    }

    public static void offerSlimeBlockDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String blockPath, String dyePath) {
        ShapelessRecipeJsonFactory.create(output)
                .input(input)
                .input(JelloTags.Items.SLIME_BLOCKS)
                .group("slime_block")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_BLOCKS))
                .offerTo(exporter, new Identifier(Jello.MODID, blockPath + "_" + dyePath));
    }

    public static void offerSlimeBlockDyeingFullRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String blockPath, String dyePath) {
        ShapedRecipeJsonFactory.create(output, 8)
                .input('#', JelloTags.Items.SLIME_BLOCKS)
                .input('X', input)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("slime_block")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_BLOCKS))
                .offerTo(exporter, new Identifier(Jello.MODID, blockPath + "_" + dyePath + "_full"));
    }

    public static void offerSlimeSlabDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String blockPath, String dyePath) {
        ShapelessRecipeJsonFactory.create(output)
                .input(input)
                .input(JelloTags.Items.SLIME_SLABS)
                .group("slime_block")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_SLABS))
                .offerTo(exporter, new Identifier(Jello.MODID, blockPath + "_" + dyePath));
    }

    public static void offerSlimeSlabDyeingFullRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String blockPath, String dyePath) {
        ShapedRecipeJsonFactory.create(output, 8)
                .input('#', JelloTags.Items.SLIME_SLABS)
                .input('X', input)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("slime_block")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_SLABS))
                .offerTo(exporter, new Identifier(Jello.MODID, blockPath + "_" + dyePath + "_full"));
    }

    public static void offerSlimeBallDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String itemPath, String dyePath) {
        ShapelessRecipeJsonFactory.create(output)
                .input(input)
                .input(JelloTags.Items.SLIME_BALLS)
                .group("slime_ball")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_BLOCKS))
                .offerTo(exporter, new Identifier(Jello.MODID, itemPath + "_" + dyePath));
    }
}
