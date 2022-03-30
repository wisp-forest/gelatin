package io.wispforest.jello.main.common.data.providers;

import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.common.data.JelloComplexRecipeJsonBuilder;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.jello.main.common.data.tags.JelloTags;
import io.wispforest.jello.main.common.recipe.RecipeSerializerRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipesProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;

public class JelloRecipeProvider extends FabricRecipesProvider {
    public JelloRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        for(int i = 0; i < DyeColorantRegistry.Constants.VANILLA_DYES.size(); i++){
            String slabPath = DyeColorantRegistry.Constants.VANILLA_DYES.get(i).getName() + "_slime_slab";
            Block slab = Registry.BLOCK.get(new Identifier(Jello.MODID, slabPath));
            //Block slab = BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.get(i);

            String blockPath = DyeColorantRegistry.Constants.VANILLA_DYES.get(i).getName() + "_slime_block";
            //SlimeBlockColored block = (SlimeBlockColored) BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.get(i);
            Block block = Registry.BLOCK.get(new Identifier(Jello.MODID, blockPath));

            Item item = ItemRegistry.SlimeBallItemRegistry.SLIME_BALLS.get(i);
            String itemPath = Registry.ITEM.getId(item).getPath();

            createSlabRecipe(slab, Ingredient.ofItems(block))
                    .criterion("has_" + Registry.BLOCK.getId(block).getPath(), conditionsFromItem(block))
                    .offerTo(exporter);

            offerReversibleCompactingRecipes(exporter, item, block);

            ShapedRecipeJsonBuilder.create(Blocks.STICKY_PISTON)
                    .input('P', Blocks.PISTON)
                    .input('S', item)
                    .pattern("S")
                    .pattern("P")
                    .criterion("has_slime_ball", conditionsFromItem(item))
                    .offerTo(exporter, new Identifier(Jello.MODID,  "sticky_piston_" + itemPath));

            ShapedRecipeJsonBuilder.create(Items.LEAD, 2)
                    .input('~', Items.STRING)
                    .input('O', item)
                    .pattern("~~ ")
                    .pattern("~O ")
                    .pattern("  ~")
                    .criterion("has_slime_ball", conditionsFromItem(item))
                    .offerTo(exporter, new Identifier(Jello.MODID,  "lead_" + itemPath));

            ShapelessRecipeJsonBuilder.create(Items.MAGMA_CREAM)
                    .input(Items.BLAZE_POWDER)
                    .input(item)
                    .criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
                    .offerTo(exporter, new Identifier(Jello.MODID,  "magma_cream" + itemPath));

            Item dyeItem = Registry.ITEM.get(new Identifier(((DyeBlockStorage)block).getDyeColor().getName() + "_dye"));
            String dyePath = ((DyeBlockStorage)block).getDyeColor().getName() + "_dye";

            offerSlimeBlockDyeingRecipe(exporter, block, dyeItem, blockPath, ((DyeBlockStorage)block).getDyeColor().getName() + "_dye");
            offerSlimeBlockDyeingFullRecipe(exporter, block, dyeItem, blockPath, ((DyeBlockStorage)block).getDyeColor().getName() + "_dye");

            offerSlimeSlabDyeingRecipe(exporter, slab, dyeItem, slabPath, ((DyeBlockStorage)block).getDyeColor().getName() + "_dye");
            offerSlimeSlabDyeingFullRecipe(exporter, slab, dyeItem, slabPath, ((DyeBlockStorage)block).getDyeColor().getName() + "_dye");

            offerSlimeBallDyeingRecipe(exporter, item, dyeItem, itemPath, dyePath);
        }

        ShapelessRecipeJsonBuilder.create(ItemRegistry.MainItemRegistry.SPONGE)
                .input(Items.WET_SPONGE)
                .input(Items.SHEARS)
                .group("")
                .criterion("has_sponge_item", conditionsFromItem(Items.WET_SPONGE))
                .criterion("has_shears_item", conditionsFromItem(Items.SHEARS))
                .offerTo(exporter, new Identifier(Jello.MODID, "sponge_item_from_wet_sponge"));

        ShapelessRecipeJsonBuilder.create(ItemRegistry.MainItemRegistry.SPONGE)
                .input(Items.SPONGE)
                .input(Items.SHEARS)
                .group("")
                .criterion("has_sponge_item", conditionsFromItem(Items.SPONGE))
                .criterion("has_shears_item", conditionsFromItem(Items.SHEARS))
                .offerTo(exporter, new Identifier(Jello.MODID, "sponge_item_from_dry_sponge"));

        JelloComplexRecipeJsonBuilder.create(RecipeSerializerRegistry.ARTIST_PALETTE).offerTo(exporter, new Identifier(Jello.MODID, "fill_artist_palette"));

        ShapelessRecipeJsonBuilder.create(ItemRegistry.MainItemRegistry.EMPTY_ARTIST_PALETTE)
                .input(Items.SHEARS)
                .input(ItemTags.WOODEN_PRESSURE_PLATES)
                .criterion("has_shears_item", conditionsFromItem(Items.SHEARS))
                .offerTo(exporter, new Identifier(Jello.MODID, "artist_palette"));
    }

    public static void offerSlimeBlockDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String blockPath, String dyePath) {
        ShapelessRecipeJsonBuilder.create(output)
                .input(input)
                .input(JelloTags.Items.SLIME_BLOCKS)
                .group("slime_block")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_BLOCKS))
                .offerTo(exporter, new Identifier(Jello.MODID, blockPath + "_" + dyePath));
    }

    public static void offerSlimeBlockDyeingFullRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String blockPath, String dyePath) {
        ShapedRecipeJsonBuilder.create(output, 8)
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
        ShapelessRecipeJsonBuilder.create(output)
                .input(input)
                .input(JelloTags.Items.SLIME_SLABS)
                .group("slime_block")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_SLABS))
                .offerTo(exporter, new Identifier(Jello.MODID, blockPath + "_" + dyePath));
    }

    public static void offerSlimeSlabDyeingFullRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String blockPath, String dyePath) {
        ShapedRecipeJsonBuilder.create(output, 8)
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
        ShapelessRecipeJsonBuilder.create(output)
                .input(input)
                .input(JelloTags.Items.SLIME_BALLS)
                .group("slime_ball")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_BLOCKS))
                .offerTo(exporter, new Identifier(Jello.MODID, itemPath + "_" + dyePath));
    }
}
