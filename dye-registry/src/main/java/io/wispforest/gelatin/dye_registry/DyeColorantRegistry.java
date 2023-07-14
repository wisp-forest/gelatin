package io.wispforest.gelatin.dye_registry;

import com.mojang.serialization.Lifecycle;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.common.util.ColorUtil;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Registry designed as a replacement for minecrafts {@link DyeColor} Enum allowing for better support for adding any DyeColor to minecraft
 */
public class DyeColorantRegistry {

    public static final RegistryKey<Registry<DyeColorant>> DYE_COLOR_KEY = RegistryKey.ofRegistry(GelatinConstants.id("dye_color"));
    public static final SimpleDefaultedRegistry<DyeColorant> DYE_COLOR = FabricRegistryBuilder.from(new SimpleDefaultedRegistry<>(GelatinConstants.id("_null").toString(), DYE_COLOR_KEY, Lifecycle.stable(), true)).buildAndRegister();

    public static final DyeColorant NULL_VALUE_NEW = registerDyeColor(GelatinConstants.id("_null"), MapColor.CLEAR, 0);

    public static final DyeColorant WHITE = registerDyeColorVanilla("white", 0xF9FFFE, MapColor.WHITE, 0xF0F0F0, 0xFFFFFF);
    public static final DyeColorant ORANGE = registerDyeColorVanilla("orange", 0xF9801D, MapColor.ORANGE, 0xEB8844, 0xFF681F);
    public static final DyeColorant MAGENTA = registerDyeColorVanilla("magenta", 0xC74EBD, MapColor.MAGENTA, 0xC354CD, 0xFF00FF);
    public static final DyeColorant LIGHT_BLUE = registerDyeColorVanilla("light_blue", 0x3AB3DA, MapColor.LIGHT_BLUE, 0x6689D3, 0x9AC0CD);
    public static final DyeColorant YELLOW = registerDyeColorVanilla("yellow", 0xFED83D, MapColor.YELLOW, 0xDECF2A, 0xFFFF00);
    public static final DyeColorant LIME = registerDyeColorVanilla("lime", 0x80C71F, MapColor.LIME, 0x41CD34, 0xBFFF00);
    public static final DyeColorant PINK = registerDyeColorVanilla("pink", 0xF38BAA, MapColor.PINK, 14188952, 16738740);
    public static final DyeColorant GRAY = registerDyeColorVanilla("gray", 4673362, MapColor.GRAY, 4408131, 8421504);
    public static final DyeColorant LIGHT_GRAY = registerDyeColorVanilla("light_gray", 10329495, MapColor.LIGHT_GRAY, 11250603, 13882323);
    public static final DyeColorant CYAN = registerDyeColorVanilla("cyan", 1481884, MapColor.CYAN, 2651799, 65535);
    public static final DyeColorant PURPLE = registerDyeColorVanilla("purple", 8991416, MapColor.PURPLE, 8073150, 10494192);
    public static final DyeColorant BLUE = registerDyeColorVanilla("blue", 3949738, MapColor.BLUE, 2437522, 255);
    public static final DyeColorant BROWN = registerDyeColorVanilla("brown", 8606770, MapColor.BROWN, 5320730, 9127187);
    public static final DyeColorant GREEN = registerDyeColorVanilla("green", 6192150, MapColor.GREEN, 3887386, 65280);
    public static final DyeColorant RED = registerDyeColorVanilla("red", 11546150, MapColor.RED, 11743532, 16711680);
    public static final DyeColorant BLACK = registerDyeColorVanilla("black", 1908001, MapColor.BLACK, 1973019, 0);

    /**
     * Method used to register and create a custom DyeColorant
     *
     * @param identifier The {@link Identifier} to be registered with your Color
     * @param mapColor   [TODO: Add the ability for custom map colors!] Only use minecraft based {@link MapColor}
     * @param baseColor  The Color as an integer color value(Hex or Decimal)
     * @return {@link DyeColorant} based off your inputted color
     */
    public static DyeColorant registerDyeColor(Identifier identifier, MapColor mapColor, int baseColor) {
        return registerDyeColor(identifier, mapColor, baseColor, baseColor, baseColor);
    }

    /**
     * Method used to register and create a custom DyeColorant
     *
     * @param identifier    The {@link Identifier} to be registered with your Color
     * @param mapColor      [TODO: Add the ability for custom map colors!] Only use minecraft based {@link MapColor}
     * @param baseColor     The Color as an integer color value(Hex or Decimal)
     * @param fireworkColor The Color as an integer color value(Hex or Decimal) shown within fireworks
     * @param signColor     The Color as an integer color value(Hex or Decimal) shown on signs
     * @return {@link DyeColorant} based off the inputted color
     */
    public static DyeColorant registerDyeColor(Identifier identifier, MapColor mapColor, int baseColor, int fireworkColor, int signColor) {
        DyeColorant dyeColor = new DyeColorant(mapColor, baseColor, fireworkColor, signColor);

        return Registry.register(DYE_COLOR, identifier, dyeColor);
    }

    //------------------------------------------------------------------------------------


    /**
     * Only used for vanilla Dye Color Registry to allow for easy dye color int Id conversion
     */
    @ApiStatus.Internal
    private static DyeColorant registerDyeColorVanilla(String dyeName, int baseColor, MapColor mapColor, int fireworkColor, int signColor) {
        DyeColorant dyeColor = registerDyeColor(new Identifier(dyeName), mapColor, baseColor, fireworkColor, signColor);

        Constants.VANILLA_DYES.add(dyeColor);

        return dyeColor;
    }



    public static DyeColorant getRandomColorant() {
        Random rand = Random.create();

        boolean nonVanillaDyeColor = false;
        RegistryEntry<DyeColorant> dyeColor = DYE_COLOR.getRandom(rand).get();

        while (!nonVanillaDyeColor) {
            if (!Constants.VANILLA_DYES.contains(dyeColor.value())) {
                nonVanillaDyeColor = true;
            } else {
                dyeColor = DYE_COLOR.getRandom(rand).get();
            }
        }

        return dyeColor.value();
    }

    public static Set<DyeColorant> getAllColorants(){
        return DYE_COLOR.stream().filter(dyeColorant -> dyeColorant != NULL_VALUE_NEW).collect(Collectors.toSet());
    }

    /**
     * Note: Code was based off of/used from <a href="https://chir.ag/projects/ntc/ntc.js">ntc.js</a>, created by Chirag Mehta,
     * under the <a href="http://creativecommons.org/licenses/by/2.5/">Creative Commons Licences</a> and retrofitted to work with Java
     *
     *<p>The method is designed to take in a integer Color value and approximate the closest color found within {@link DyeColorantRegistry#DYE_COLOR}</p>
     */
    public static DyeColorant getNearestColorant(int colorValue) {
        var rgb = new int[]{colorValue >> 16, (colorValue >> 8) & 0xFF, colorValue & 0xFF};
        var r = rgb[0];
        var g = rgb[1];
        var b = rgb[2];

        var hsl = ColorUtil.rgbToHsl(colorValue);
        var h = hsl[0];
        var s = hsl[1];
        var l = hsl[2];

        float ndf1;
        float ndf2;
        float ndf;
        int cl = -1;
        float df = -1;

        List<DyeColorant> dyeColorantList = DYE_COLOR.stream().toList();

        for (var i = 0; i < dyeColorantList.size(); i++) {
            DyeColorant currentDyeColor = dyeColorantList.get(i);

            int closeColorValue = currentDyeColor.getBaseColor();

            int[] rgbColorArray = new int[]{closeColorValue >> 16, (closeColorValue >> 8) & 0xFF, closeColorValue & 0xFF};

            float[] hslColorArray = ColorUtil.rgbToHsl(closeColorValue);

            if (colorValue == closeColorValue)
                return currentDyeColor;

            ndf1 = MathHelper.square(r - rgbColorArray[0]) + MathHelper.square(g - rgbColorArray[1]) + MathHelper.square(b - rgbColorArray[2]);//ntc.names[i][2], 2) + Math.pow(g - ntc.names[i][3], 2) + Math.pow(b - ntc.names[i][4], 2);
            ndf2 = MathHelper.square(h - hslColorArray[0]) + MathHelper.square(s - hslColorArray[1]) + MathHelper.square(l - hslColorArray[2]);//ntc.names[i][5], 2) + Math.pow(s - ntc.names[i][6], 2) + Math.pow(l - ntc.names[i][7], 2);
            ndf = ndf1 + ndf2 * 2;
            if (df < 0 || df > ndf) {
                df = ndf;
                cl = i;
            }
        }

        return dyeColorantList.get(cl);
    }

    public static class Constants {

        public static final List<DyeColorant> VANILLA_DYES = new ArrayList<>();

        public static final String ENUM_NAMESPACE = "enum";

        @ApiStatus.Internal
        public static net.minecraft.util.DyeColor NULL_VALUE_OLD;
    }
}
