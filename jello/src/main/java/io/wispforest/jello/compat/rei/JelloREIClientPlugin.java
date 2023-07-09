package io.wispforest.jello.compat.rei;

import io.wispforest.gelatin.dye_entries.variants.impl.VanillaItemVariants;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.jello.Jello;
import io.wispforest.jello.item.ArtistPaletteItem;
import io.wispforest.jello.item.jellocup.JelloCupCreationHandler;
import io.wispforest.jello.item.jellocup.JelloCupItem;
import io.wispforest.jello.item.JelloItems;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.ShapedRecipe;

import java.util.*;
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

        for (JelloCupCreationHandler handler : JelloCupCreationHandler.ALL_CUP_DATA.values()) {
            List<ItemStack> ingredientStacks;

            if(!handler.alternativeValidIngredient.isEmpty()){
                ingredientStacks = handler.alternativeValidIngredient.keySet().stream()
                        .map(Item::getDefaultStack)
                        .toList();
            } else {
                ingredientStacks = handler.validPotions.stream()
                        .map(potion -> PotionUtil.setPotion(new ItemStack(Items.POTION), potion))
                        .toList();
            }

            registry.add(GelatinSolutionDisplay.of(ingredientStacks, handler));
            registry.add(JelloCupDisplay.of(ingredientStacks, handler));
        }
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ColorMixerCategory());
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.removeEntry(EntryStacks.of(JelloItems.GELATIN_SOLUTION));
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

    private static class GelatinSolutionDisplay extends DefaultCraftingDisplay<ShapedRecipe> {
        public GelatinSolutionDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs){
            super(inputs, outputs, Optional.empty());
        }

        public static GelatinSolutionDisplay of(Collection<ItemStack> ingredientStacks, JelloCupCreationHandler handler){
            List<EntryIngredient> inputs = new ArrayList<>();

            inputs.add(EntryIngredients.of(Items.SUGAR));
            inputs.add(EntryIngredients.of(Items.WATER_BUCKET));
            inputs.add(EntryIngredients.of(JelloItems.GELATIN));

            List<ItemStack> solutionStacks = new ArrayList<>();

            for (ItemStack stack : ingredientStacks) solutionStacks.add(handler.buildSolution(stack));

            inputs.add(EntryIngredients.ofItemStacks(ingredientStacks));

            return new GelatinSolutionDisplay(inputs, Collections.singletonList(EntryIngredients.ofItemStacks(solutionStacks)));
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

    private static class JelloCupDisplay extends DefaultCraftingDisplay<ShapedRecipe> {
        public JelloCupDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs){
            super(inputs, outputs, Optional.empty());
        }

        public static JelloCupDisplay of(Collection<ItemStack> ingredientStacks, JelloCupCreationHandler handler){
            List<EntryIngredient> inputs = new ArrayList<>();

            List<ItemStack> solutionStacks = new ArrayList<>();

            for (ItemStack stack : ingredientStacks) solutionStacks.add(handler.buildSolution(stack));

            inputs.add(EntryIngredients.ofItemStacks(solutionStacks));

            inputs.add(EntryIngredients.of(JelloItems.JelloCups.SUGAR_CUP));
            inputs.add(EntryIngredients.of(JelloItems.JelloCups.SUGAR_CUP));
            inputs.add(EntryIngredients.of(JelloItems.JelloCups.SUGAR_CUP));

            List<ItemStack> jelloCups = new ArrayList<>();

            for (ItemStack stack : solutionStacks) jelloCups.add(handler.buildJelloCup(stack));

            return new JelloCupDisplay(inputs, Collections.singletonList(EntryIngredients.ofItemStacks(jelloCups)));
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
