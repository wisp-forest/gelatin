package io.wispforest.jello.api.dye.registry;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.api.mixin.mixins.dye.SimpleRegistryAccessor;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.MapColor;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class DyeColorantRegistry {

    public static final RegistryKey<Registry<DyeColorant>> DYE_COLOR_KEY = RegistryKey.ofRegistry(new Identifier(Jello.MODID, "dye_color"));

    public static final DefaultedRegistry<DyeColorant> DYE_COLOR = FabricRegistryBuilder.createDefaulted(DyeColorant.class, DYE_COLOR_KEY.getValue(), new Identifier(Jello.MODID, "_null")).buildAndRegister();

    //Fix for fabric not allowing for a function to be passed thru
    static{
        ((SimpleRegistryAccessor)DYE_COLOR).setValueToEntryFunction(dyeColor -> ((DyeColorant)dyeColor).getRegistryEntry());
        ((SimpleRegistryAccessor)DYE_COLOR).setUnfrozenValueToEntry(new IdentityHashMap());
    }

    public static final Set<Identifier> IDENTIFIER_RESOURCE_REDIRECTS = new HashSet<>();
    public static final Set<String> NAMESPACE_RESOURCE_REDIRECTS = new HashSet<>();

    public static final DyeColorant NULL_VALUE_NEW = registryDyeColor(new Identifier(Jello.MODID, "_null"),  MapColor.CLEAR, 0);

    public static final DyeColorant WHITE = registryDyeColorVanilla("white", 0xF9FFFE, MapColor.WHITE, 0xF0F0F0, 0xFFFFFF);
    public static final DyeColorant ORANGE = registryDyeColorVanilla("orange", 0xF9801D, MapColor.ORANGE, 0xEB8844, 0xFF681F);
    public static final DyeColorant MAGENTA = registryDyeColorVanilla("magenta", 0xC74EBD, MapColor.MAGENTA, 0xC354CD, 0xFF00FF);
    public static final DyeColorant LIGHT_BLUE = registryDyeColorVanilla("light_blue", 0x3AB3DA, MapColor.LIGHT_BLUE, 0x6689D3, 0x9AC0CD);
    public static final DyeColorant YELLOW = registryDyeColorVanilla("yellow", 0xFED83D, MapColor.YELLOW, 0xDECF2A, 0xFFFF00);
    public static final DyeColorant LIME = registryDyeColorVanilla("lime", 0x80C71F, MapColor.LIME, 0x41CD34, 0xBFFF00);
    public static final DyeColorant PINK = registryDyeColorVanilla("pink", 0xF38BAA, MapColor.PINK, 14188952, 16738740);
    public static final DyeColorant GRAY = registryDyeColorVanilla("gray", 4673362, MapColor.GRAY, 4408131, 8421504);
    public static final DyeColorant LIGHT_GRAY = registryDyeColorVanilla("light_gray", 10329495, MapColor.LIGHT_GRAY, 11250603, 13882323);
    public static final DyeColorant CYAN = registryDyeColorVanilla("cyan", 1481884, MapColor.CYAN, 2651799, 65535);
    public static final DyeColorant PURPLE = registryDyeColorVanilla("purple", 8991416, MapColor.PURPLE, 8073150, 10494192);
    public static final DyeColorant BLUE = registryDyeColorVanilla("blue", 3949738, MapColor.BLUE, 2437522, 255);
    public static final DyeColorant BROWN = registryDyeColorVanilla("brown", 8606770, MapColor.BROWN, 5320730, 9127187);
    public static final DyeColorant GREEN = registryDyeColorVanilla("green", 6192150, MapColor.GREEN, 3887386, 65280);
    public static final DyeColorant RED = registryDyeColorVanilla("red", 11546150, MapColor.RED, 11743532, 16711680);
    public static final DyeColorant BLACK = registryDyeColorVanilla("black", 1908001, MapColor.BLACK, 1973019, 0);

    public static void initVanillaDyes() {
        for(DyeColorant dyeColorant : Constants.VANILLA_DYES){
            DyedVariantContainer.createVariantContainer(dyeColorant);
        }
    }

    /**
     * Method used to register and create a custom DyeColorant
     *
     * @param identifier The {@link Identifier} to be registered with your Color
     * @param mapColor [TODO: Add the ability for custom map colors!] Only use minecraft based {@link MapColor}
     * @param baseColor The Color as an integer color value(Hex or Decimal)
     * @return {@link DyeColorant} based off your inputted color
     */
    public static DyeColorant registryDyeColor(Identifier identifier, MapColor mapColor, int baseColor){
        return registryDyeColor(identifier, mapColor, baseColor, baseColor, baseColor);
    }

    /**
     * Method used to register and create a custom DyeColorant
     *
     * @param identifier The {@link Identifier} to be registered with your Color
     * @param mapColor [TODO: Add the ability for custom map colors!] Only use minecraft based {@link MapColor}
     * @param baseColor The Color as an integer color value(Hex or Decimal)
     * @param fireworkColor The Color as an integer color value(Hex or Decimal) shown within fireworks
     * @param signColor The Color as an integer color value(Hex or Decimal) shown on signs
     * @return {@link DyeColorant} based off the inputted color
     */
    public static DyeColorant registryDyeColor(Identifier identifier, MapColor mapColor, int baseColor, int fireworkColor, int signColor){
        DyeColorant dyeColor = new DyeColorant(mapColor, baseColor, fireworkColor, signColor);

        return Registry.register(DYE_COLOR, identifier, dyeColor);
    }

    //------------------------------------------------------------------------------------

    /**
     * Creates a bunch of Dyed Variants of the inputted {@link DyeColorant}
     *
     * @param dyeColorant The {@link DyeColorant} you want to base all the {@link DyedVariantContainer} off
     * @param itemSettings The settings for the DyeItem being created based off your dye
     * @return {@link DyedVariantContainer} based off the inputted {@link DyeColorant}
     */
    public static DyedVariantContainer createDyedVariants(DyeColorant dyeColorant, Item.Settings itemSettings){
        return createDyedVariants(dyeColorant, itemSettings, true);
    }

    /**
     * Creates a bunch of Dyed Variants of the inputted {@link DyeColorant}
     *
     * @param dyeColorant The {@link DyeColorant} you want to base all the {@link DyedVariantContainer} off
     * @param itemSettings The settings for the DyeItem being created based off your dye
     * @param identifierModelRedirect Used to enable or disable model redirect if you're using custom models for the block and item variants
     * @return {@link DyedVariantContainer} based off the inputted {@link DyeColorant}
     */
    public static DyedVariantContainer createDyedVariants(DyeColorant dyeColorant, Item.Settings itemSettings, boolean identifierModelRedirect){
        return DyedVariantContainer.createVariantContainer(dyeColorant, itemSettings, identifierModelRedirect);
    }

    //------------------------------------------------------------------------------------

    /**
     *  Only used for vanilla Dye Color Registry to allow for easy dye color int Id conversion
     */
    @ApiStatus.Internal
    private static DyeColorant registryDyeColorVanilla(String dyeName, int baseColor, MapColor mapColor, int fireworkColor, int signColor){
        DyeColorant dyeColor = registryDyeColor(new Identifier(dyeName), mapColor, baseColor, fireworkColor, signColor);

        Constants.VANILLA_DYES.add(dyeColor);

        return dyeColor;
    }

    public static void registerModidModelRedirect(String modid){
        NAMESPACE_RESOURCE_REDIRECTS.add(modid);
    }

    @ApiStatus.Internal
    public static boolean shouldRedirectModelResource(Identifier identifier){
        if(NAMESPACE_RESOURCE_REDIRECTS.contains(identifier.getNamespace())){
            return true;
        }

        return IDENTIFIER_RESOURCE_REDIRECTS.contains(identifier);
    }

    public static class Constants{

        public static final List<DyeColorant> VANILLA_DYES = new ArrayList<>();

        public static final String ENUM_NAMESPACE = "enum";

        @ApiStatus.Internal
        public static net.minecraft.util.DyeColor NULL_VALUE_OLD;
    }


}
