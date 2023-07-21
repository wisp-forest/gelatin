package io.wispforest.gelatin.dye_entries.utils;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantManager;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

public class DyeVariantBuilder {

    /**
     * Creates a bunch of Dyed Variants of the inputted {@link DyeColorant}
     *
     * @param dyeColorant  The {@link DyeColorant} you want to base all the {@link DyeableVariantManager} off
     * @param itemSettings The settings for the DyeItem being created based off your dyeColorant
     * @return {@link DyeableVariantManager} based off the inputted {@link DyeColorant}
     */
    public static DyeableVariantManager.DyeColorantVariantData createDyedVariants(DyeColorant dyeColorant, Item.Settings itemSettings) {
        return createDyedVariants(dyeColorant, itemSettings, true);
    }

    /**
     * Creates a bunch of Dyed Variants of the inputted {@link DyeColorant}
     *
     * @param dyeColorant             The {@link DyeColorant} you want to base all the {@link DyeableVariantManager} off
     * @param itemSettings            The settings for the DyeItem being created based off your dye
     * @param identifierModelRedirect Used to enable or disable model redirect if you're using custom models for the block and item variants
     * @return {@link DyeableVariantManager} based off the inputted {@link DyeColorant}
     */
    public static DyeableVariantManager.DyeColorantVariantData createDyedVariants(DyeColorant dyeColorant, Item.Settings itemSettings, boolean identifierModelRedirect) {
        return DyeableVariantManager.createVariantContainer(dyeColorant, itemSettings, identifierModelRedirect);
    }

    public static void initVanillaDyes() {
        for (DyeColorant dyeColorant : DyeColorantRegistry.Constants.VANILLA_DYES) {
            DyeableVariantManager.createVariantContainer(dyeColorant);
        }
    }
}
