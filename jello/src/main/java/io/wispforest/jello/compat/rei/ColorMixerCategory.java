package io.wispforest.jello.compat.rei;

import io.wispforest.jello.Jello;
import io.wispforest.jello.block.JelloBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ColorMixerCategory implements DisplayCategory<ColorMixerDisplay> {

    private static final Text NAME = Text.translatable("jello.gui.dye_mixer");

    @Override
    public CategoryIdentifier<? extends ColorMixerDisplay> getCategoryIdentifier() {
        return JelloREIClientPlugin.DYE_MIXING;
    }

    @Override
    public Text getTitle() {
        return NAME;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(JelloBlocks.PAINT_MIXER);
    }

    @Override
    public int getDisplayHeight() {
        return 82;
    }


    @Override
    public int getDisplayWidth(ColorMixerDisplay display) {
        return 141;
    }

    @Override
    public List<Widget> setupDisplay(ColorMixerDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 7, bounds.getY() + 58)).entries(display.getInputEntries().get(0)).disableBackground());
        widgets.add(Widgets.createSlot(new Point(bounds.getX() + 28, bounds.getY() + 22)).entries(display.getOutputEntries().get(0)).disableBackground());

        widgets.add(Widgets.createTexturedWidget(Jello.id("textures/gui/color_mixer_rei.png"), bounds.getX(), bounds.getY(), 0, 0, 141, 82, 160, 96));

        if(display.getOutputEntries().get(0).get(0).getValue() instanceof ItemStack stack){
            widgets.add(Widgets.createLabel(new Point(bounds.getX() + 29, bounds.getY() + 8), Text.translatable(stack.getItem().getTranslationKey())).leftAligned());
        }

        return widgets;
    }
}
