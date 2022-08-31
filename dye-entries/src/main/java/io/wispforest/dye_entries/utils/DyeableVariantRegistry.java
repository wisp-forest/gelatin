package io.wispforest.dye_entries.utils;

import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;
import io.wispforest.dye_entries.variants.DyeableVariantManager;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

public class DyeableVariantRegistry {

    public static final Set<Identifier> IDENTIFIER_RESOURCE_REDIRECTS = new HashSet<>();
    public static final Set<String> NAMESPACE_RESOURCE_REDIRECTS = new HashSet<>();

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

    /**
     * [Warning]: This a faster method to identifier Model Redirect but could cause issues loading some models.
     * <p>
     * Simple method to add your MODID within the Model Redirect System for your created Variants
     *
     * @param modid A string representing your mods Id
     */
    public static void registerModidModelRedirect(String modid) {
        NAMESPACE_RESOURCE_REDIRECTS.add(modid);
    }

    @ApiStatus.Internal
    public static boolean shouldRedirectModelResource(Identifier identifier) {
        if (NAMESPACE_RESOURCE_REDIRECTS.contains(identifier.getNamespace())) {
            return true;
        }

        return IDENTIFIER_RESOURCE_REDIRECTS.contains(identifier);
    }

    public static void initVanillaDyes() {
        for (DyeColorant dyeColorant : DyeColorantRegistry.Constants.VANILLA_DYES) {
            DyeableVariantManager.createVariantContainer(dyeColorant);
        }
    }
}
