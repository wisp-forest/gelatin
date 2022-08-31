package io.wispforest.jello.compat.rei;

import io.wispforest.dye_entries.variants.VanillaItemVariants;
import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;
import io.wispforest.jello.Jello;
import io.wispforest.jello.item.ArtistPaletteItem;
import io.wispforest.jello.item.JelloItems;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.ShapedRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JelloREIClientPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<ColorMixerDisplay> DYE_MIXING = CategoryIdentifier.of(Jello.id("dye_mixing"));

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for(DyeColorant dyeColorant : DyeColorantRegistry.DYE_COLOR){
            if(dyeColorant == DyeColorantRegistry.NULL_VALUE_NEW) continue;

            registry.add(new ColorMixerDisplay(dyeColorant));
        }

        registry.add(ArtistPaletteDisplay.of());
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ColorMixerCategory());
    }

    private static class ArtistPaletteDisplay extends DefaultCraftingDisplay<ShapedRecipe> {
        public ArtistPaletteDisplay(List<EntryIngredient> inputs){
            super(inputs,
                    Collections.singletonList(EntryIngredients.of(JelloItems.ARTIST_PALETTE.getDefaultStack())),
                    Optional.empty()
            );
        }

        public static ArtistPaletteDisplay of(){
            List<EntryIngredient> inputs = new ArrayList<>();

            List<ItemConvertible> ingredientItems = ArtistPaletteItem.ALLOWED_COLORS.stream().map(VanillaItemVariants.DYE::getColoredEntry).collect(Collectors.toList());

            rotateAndAddEntries(ingredientItems, inputs);
            rotateAndAddEntries(ingredientItems, inputs);
            rotateAndAddEntries(ingredientItems, inputs);

            rotateAndAddEntries(ingredientItems, inputs);
            inputs.add(EntryIngredients.of(JelloItems.EMPTY_ARTIST_PALETTE));
            inputs.add(EntryIngredients.ofItems(ingredientItems));

            inputs.set(3, inputs.set(5, inputs.get(3)));

            inputs.add(EntryIngredients.of(Items.AIR));
            inputs.add(EntryIngredients.of(Items.AIR));
            inputs.add(EntryIngredients.of(Items.AIR));

            return new ArtistPaletteDisplay(inputs);
        }

        public static void rotateAndAddEntries(List<ItemConvertible> ingredientItems, List<EntryIngredient> inputs){
            inputs.add(EntryIngredients.ofItems(ingredientItems));

            rotateList(ingredientItems);
        }

        public static <T> void rotateList(List<T> list){
            list.add(0, list.remove(list.size() - 1));
        }

        @Override
        public int getWidth() {
            return 3;
        }

        @Override
        public int getHeight() {
            return 3;
        }
    }
}
