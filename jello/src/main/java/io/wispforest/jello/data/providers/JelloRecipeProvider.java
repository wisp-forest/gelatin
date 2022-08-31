package io.wispforest.jello.data.providers;

import io.wispforest.jello.data.GelatinComplexRecipeJsonBuilder;
import io.wispforest.dye_registry.DyeColorantRegistry;
import io.wispforest.dye_registry.data.GelatinTags;
import io.wispforest.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.jello.Jello;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.data.JelloTags;
import io.wispforest.jello.data.recipe.JelloRecipeSerializers;
import io.wispforest.jello.item.JelloItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

public class JelloRecipeProvider extends FabricRecipeProvider {
    public JelloRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        for (int i = 0; i < DyeColorantRegistry.Constants.VANILLA_DYES.size(); i++) {
            String slabPath = DyeColorantRegistry.Constants.VANILLA_DYES.get(i).getName() + "_slime_slab";
            Block slab = Registry.BLOCK.get(Jello.id(slabPath));

            String blockPath = DyeColorantRegistry.Constants.VANILLA_DYES.get(i).getName() + "_slime_block";
            Block block = Registry.BLOCK.get(Jello.id(blockPath));

            Item item = JelloItems.Slimeballs.SLIME_BALLS.get(i);
            String itemPath = Registry.ITEM.getId(item).getPath();

            createSlabRecipe(slab, Ingredient.ofItems(block))
                    .criterion("has_" + Registry.BLOCK.getId(block).getPath(), conditionsFromItem(block))
                    .offerTo(exporter);

            offerReversibleCompactingRecipes(exporter, item, block);

            Item dyeItem = Registry.ITEM.get(new Identifier(((DyeBlockStorage) block).getDyeColorant().getName() + "_dye"));
            String dyePath = ((DyeBlockStorage) block).getDyeColorant().getName() + "_dye";

//            offerSlimeBlockDyeingRecipe(exporter, block, dyeItem, blockPath, ((DyeBlockStorage) block).getDyeColor().getName() + "_dye");
//            offerSlimeBlockDyeingFullRecipe(exporter, block, dyeItem, blockPath, ((DyeBlockStorage) block).getDyeColor().getName() + "_dye");
//
//            offerSlimeSlabDyeingRecipe(exporter, slab, dyeItem, slabPath, ((DyeBlockStorage) block).getDyeColor().getName() + "_dye");
//            offerSlimeSlabDyeingFullRecipe(exporter, slab, dyeItem, slabPath, ((DyeBlockStorage) block).getDyeColor().getName() + "_dye");

            offerSlimeBallDyeingRecipe(exporter, item, dyeItem, itemPath, dyePath);
        }

        ShapedRecipeJsonBuilder.create(Items.BUNDLE)
                .input('~', Items.STRING)
                .input('H', Items.RABBIT_HIDE)
                .pattern("~H~")
                .pattern("H H")
                .pattern("HHH")
                .criterion("has_rabbit_hide", conditionsFromItem(Items.RABBIT_HIDE))
                .offerTo(exporter, new Identifier("bundle_from_rabbit_hide"));

        ShapedRecipeJsonBuilder.create(Items.BUNDLE)
                .input('~', Items.STRING)
                .input('H', Items.LEATHER)
                .pattern("~H~")
                .pattern("H H")
                .pattern("HHH")
                .criterion("has_rabbit_hide", conditionsFromItem(Items.RABBIT_HIDE))
                .offerTo(exporter, new Identifier("bundle_from_leather"));

        ShapedRecipeJsonBuilder.create(JelloItems.DYE_BUNDLE)
                .input('b', Items.BUNDLE)
                .input('~', GelatinTags.Items.VANILLA_DYE)
                .pattern("~~~")
                .pattern("~b~")
                .pattern("~~~")
                .criterion("has_bundle", conditionsFromItem(Items.BUNDLE))
                .offerTo(exporter, new Identifier("dye_bundle"));

        ShapedRecipeJsonBuilder.create(Blocks.STICKY_PISTON)
                .input('P', Blocks.PISTON)
                .input('S', JelloTags.Items.SLIME_BALLS)
                .pattern("S")
                .pattern("P")
                .criterion("has_slime_ball", conditionsFromTag(JelloTags.Items.SLIME_BALLS))
                .offerTo(exporter, new Identifier("sticky_piston"));

        ShapedRecipeJsonBuilder.create(Items.LEAD, 2)
                .input('~', Items.STRING)
                .input('O', JelloTags.Items.SLIME_BALLS)
                .pattern("~~ ")
                .pattern("~O ")
                .pattern("  ~")
                .criterion("has_slime_ball", conditionsFromTag(JelloTags.Items.SLIME_BALLS))
                .offerTo(exporter, new Identifier("lead"));

        ShapelessRecipeJsonBuilder.create(Items.MAGMA_CREAM)
                .input(Items.BLAZE_POWDER)
                .input(JelloTags.Items.SLIME_BALLS)
                .criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
                .offerTo(exporter, new Identifier("magma_cream"));

        ShapelessRecipeJsonBuilder.create(JelloItems.SPONGE)
                .input(Items.WET_SPONGE)
                .input(Items.SHEARS)
                .group("")
                .criterion("has_sponge_item", conditionsFromItem(Items.WET_SPONGE))
                .criterion("has_shears_item", conditionsFromItem(Items.SHEARS))
                .offerTo(exporter, Jello.id("sponge_item_from_wet_sponge"));

        ShapelessRecipeJsonBuilder.create(JelloItems.SPONGE)
                .input(Items.SPONGE)
                .input(Items.SHEARS)
                .group("")
                .criterion("has_sponge_item", conditionsFromItem(Items.SPONGE))
                .criterion("has_shears_item", conditionsFromItem(Items.SHEARS))
                .offerTo(exporter, Jello.id("sponge_item_from_dry_sponge"));

        GelatinComplexRecipeJsonBuilder.create(JelloRecipeSerializers.ARTIST_PALETTE).offerTo(exporter, Jello.id("fill_artist_palette"));

        ShapelessRecipeJsonBuilder.create(JelloItems.EMPTY_ARTIST_PALETTE)
                .input(Items.SHEARS)
                .input(ItemTags.WOODEN_PRESSURE_PLATES)
                .criterion("has_shears_item", conditionsFromItem(Items.SHEARS))
                .offerTo(exporter, Jello.id("artist_palette"));

        ShapedRecipeJsonBuilder.create(JelloBlocks.PAINT_MIXER)
                .input('l', Items.LAPIS_LAZULI)
                .input('c', Blocks.CAULDRON)
                .input('p', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .pattern("lpl")
                .pattern("lcl")
                .pattern("lll")
                .criterion("has_cauldron", conditionsFromItem(Blocks.CAULDRON))
                .offerTo(exporter, Jello.id("paint_mixer"));
    }

    public static void offerSlimeBlockDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String blockPath, String dyePath) {
        ShapelessRecipeJsonBuilder.create(output)
                .input(input)
                .input(JelloTags.Items.SLIME_BLOCKS)
                .group("slime_block")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_BLOCKS))
                .offerTo(exporter, Jello.id(blockPath + "_" + dyePath));
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
                .offerTo(exporter, Jello.id(blockPath + "_" + dyePath + "_full"));
    }

    //TODO: CONVERT TO SPECIAL RECIPE!!!! CAUSES HUGE LAG
    public static void offerSlimeSlabDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String blockPath, String dyePath) {
        ShapelessRecipeJsonBuilder.create(output)
                .input(input)
                .input(JelloTags.Items.SLIME_SLABS)
                .group("slime_block")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_SLABS))
                .offerTo(exporter, Jello.id(blockPath + "_" + dyePath));
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
                .offerTo(exporter, Jello.id(blockPath + "_" + dyePath + "_full"));
    }

    public static void offerSlimeBallDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, String itemPath, String dyePath) {
        ShapelessRecipeJsonBuilder.create(output)
                .input(input)
                .input(JelloTags.Items.SLIME_BALLS)
                .group("slime_ball")
                .criterion("has_slime_block_var", conditionsFromTag(JelloTags.Items.SLIME_BLOCKS))
                .offerTo(exporter, Jello.id(itemPath + "_" + dyePath));
    }
}
