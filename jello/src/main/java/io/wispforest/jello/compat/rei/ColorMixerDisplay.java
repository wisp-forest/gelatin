package io.wispforest.jello.compat.rei;

import io.wispforest.gelatin.dye_entries.variants.impl.VanillaItemVariants;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.jello.item.JelloItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.List;

public class ColorMixerDisplay implements Display {

    protected final List<EntryIngredient> input;
    protected final List<EntryIngredient> output;

    public ColorMixerDisplay(DyeColorant dyeColorant){
        this.input = List.of(EntryIngredients.of(JelloItems.ARTIST_PALETTE.getDefaultStack()));
        this.output = List.of(EntryIngredients.of(VanillaItemVariants.DYE.getColoredEntry(dyeColorant)));
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return JelloREIClientPlugin.DYE_MIXING;
    }
}
