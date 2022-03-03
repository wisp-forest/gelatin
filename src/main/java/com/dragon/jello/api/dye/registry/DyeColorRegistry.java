package com.dragon.jello.api.dye.registry;

import com.dragon.jello.api.dye.DyeColorant;
import com.dragon.jello.main.common.Jello;
import com.dragon.jello.main.common.Util.ColorUtil;
import com.dragon.jello.api.mixin.mixins.dye.SimpleRegistryAccessor;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.MapColor;
import net.minecraft.item.DyeItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class DyeColorRegistry {

    public static final RegistryKey<Registry<DyeColorant>> DYE_COLOR_KEY = RegistryKey.ofRegistry(new Identifier(Jello.MODID, "dye_color"));

    public static final DefaultedRegistry<DyeColorant> DYE_COLOR = FabricRegistryBuilder.createDefaulted(DyeColorant.class, DYE_COLOR_KEY.getValue(), new Identifier(Jello.MODID, "_null")).buildAndRegister();

    //Fix for fabric not allowing for a function to be passed thru
    static{
        ((SimpleRegistryAccessor)DYE_COLOR).setValueToEntryFunction(dyeColor -> ((DyeColorant)dyeColor).getRegistryEntry());
        ((SimpleRegistryAccessor)DYE_COLOR).setUnfrozenValueToEntry(new IdentityHashMap());
    }

    public static final Map<DyeColorant, DyeItem> DYE_COLOR_TO_DYEITEM = new HashMap<>();

    public static final List<DyeColorant> VANILLA_DYES = new ArrayList<>();

    @ApiStatus.Internal
    public static net.minecraft.util.DyeColor NULL_VALUE_OLD;

    public static final DyeColorant NULL_VALUE_NEW = registryDyeColor(new Identifier(Jello.MODID, "_null"), 0, MapColor.CLEAR);

    public static final DyeColorant WHITE = registryDyeColorVanilla("white", 16383998, MapColor.WHITE, 15790320, 16777215);
    public static final DyeColorant ORANGE = registryDyeColorVanilla("orange", 16351261, MapColor.ORANGE, 15435844, 16738335);
    public static final DyeColorant MAGENTA = registryDyeColorVanilla("magenta", 13061821, MapColor.MAGENTA, 12801229, 16711935);
    public static final DyeColorant LIGHT_BLUE = registryDyeColorVanilla("light_blue", 3847130, MapColor.LIGHT_BLUE, 6719955, 10141901);
    public static final DyeColorant YELLOW = registryDyeColorVanilla("yellow", 16701501, MapColor.YELLOW, 14602026, 16776960);
    public static final DyeColorant LIME = registryDyeColorVanilla("lime", 8439583, MapColor.LIME, 4312372, 12582656);
    public static final DyeColorant PINK = registryDyeColorVanilla("pink", 15961002, MapColor.PINK, 14188952, 16738740);
    public static final DyeColorant GRAY = registryDyeColorVanilla("gray", 4673362, MapColor.GRAY, 4408131, 8421504);
    public static final DyeColorant LIGHT_GRAY = registryDyeColorVanilla("light_gray", 10329495, MapColor.LIGHT_GRAY, 11250603, 13882323);
    public static final DyeColorant CYAN = registryDyeColorVanilla("cyan", 1481884, MapColor.CYAN, 2651799, 65535);
    public static final DyeColorant PURPLE = registryDyeColorVanilla( "purple", 8991416, MapColor.PURPLE, 8073150, 10494192);
    public static final DyeColorant BLUE = registryDyeColorVanilla( "blue", 3949738, MapColor.BLUE, 2437522, 255);
    public static final DyeColorant BROWN = registryDyeColorVanilla( "brown", 8606770, MapColor.BROWN, 5320730, 9127187);
    public static final DyeColorant GREEN = registryDyeColorVanilla( "green", 6192150, MapColor.GREEN, 3887386, 65280);
    public static final DyeColorant RED = registryDyeColorVanilla( "red", 11546150, MapColor.RED, 11743532, 16711680);
    public static final DyeColorant BLACK = registryDyeColorVanilla( "black", 1908001, MapColor.BLACK, 1973019, 0);

    public static DyeColorant registryDyeColor(Identifier id, int baseColor, MapColor mapColor){
        return registryDyeColorNameOverride(id, id.getPath(), baseColor, mapColor);
    }

    public static DyeColorant registryDyeColorNameOverride(Identifier id, String overrideDisplayName, int baseColor, MapColor mapColor){
        return registryDyeColor(id, overrideDisplayName, mapColor, baseColor, baseColor, baseColor);
    }

    /**
     *  [Warning]: Only used for vanilla Dye Color Registry to allow for easy dye color int Id conversion
     *
     * @param dyeName
     * @param baseColor
     * @param mapColor
     * @param fireworkColor
     * @param signColor
     * @return DyeColor
     */
    @ApiStatus.Internal
    private static DyeColorant registryDyeColorVanilla(String dyeName, int baseColor, MapColor mapColor, int fireworkColor, int signColor){
        DyeColorant dyeColor = registryDyeColor(new Identifier(dyeName), dyeName, mapColor, baseColor, fireworkColor, signColor);

        //DYE_COLOR_INT_MAP.add(new Identifier(dyeName));
        VANILLA_DYES.add(dyeColor);

        return dyeColor;
    }

    public static DyeColorant registryDyeColor(Identifier id, String displayName, MapColor mapColor, int baseColor, int fireworkColor, int signColor){
        DyeColorant dyeColor = new DyeColorant(displayName, ColorUtil.getColorComponents(baseColor),  mapColor, baseColor, fireworkColor, signColor);

        return Registry.register(DYE_COLOR, id, dyeColor);
    }

    //------------------------------------------------------------------------------------
}
