package com.dragon.jello.dyelib;

import com.dragon.jello.common.Jello;
import com.dragon.jello.common.Util.ColorUtil;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.DyeItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DyeColorRegistry {

    private static final List<Identifier> DYE_COLOR_INT_MAP = new ArrayList<>();

    public static final Map<Integer, DyeColor> BY_FIREWORK_COLOR = new HashMap<>();
    public static final Map<DyeColor, DyeItem> DYE_COLOR_TO_DYEITEM = new HashMap<>();

    public static final List<DyeColor> VANILLA_DYES = new ArrayList<>();

    public static final RegistryKey<Registry<Block>> DYE_COLOR_KEY = RegistryKey.ofRegistry(new Identifier(Jello.MODID, "dye_color"));
    public static final DefaultedRegistry<DyeColor> DYE_COLOR = FabricRegistryBuilder.createDefaulted(DyeColor.class, DYE_COLOR_KEY.getValue(), new Identifier("white")).buildAndRegister();

    public static final DyeColor WHITE = registryDyeColorVanilla("white", 16383998, MapColor.WHITE, 15790320, 16777215);
    public static final DyeColor ORANGE = registryDyeColorVanilla("orange", 16351261, MapColor.ORANGE, 15435844, 16738335);
    public static final DyeColor MAGENTA = registryDyeColorVanilla("magenta", 13061821, MapColor.MAGENTA, 12801229, 16711935);
    public static final DyeColor LIGHT_BLUE = registryDyeColorVanilla("light_blue", 3847130, MapColor.LIGHT_BLUE, 6719955, 10141901);
    public static final DyeColor YELLOW = registryDyeColorVanilla("yellow", 16701501, MapColor.YELLOW, 14602026, 16776960);
    public static final DyeColor LIME = registryDyeColorVanilla("lime", 8439583, MapColor.LIME, 4312372, 12582656);
    public static final DyeColor PINK = registryDyeColorVanilla("pink", 15961002, MapColor.PINK, 14188952, 16738740);
    public static final DyeColor GRAY = registryDyeColorVanilla("gray", 4673362, MapColor.GRAY, 4408131, 8421504);
    public static final DyeColor LIGHT_GRAY = registryDyeColorVanilla("light_gray", 10329495, MapColor.LIGHT_GRAY, 11250603, 13882323);
    public static final DyeColor CYAN = registryDyeColorVanilla("cyan", 1481884, MapColor.CYAN, 2651799, 65535);
    public static final DyeColor PURPLE = registryDyeColorVanilla( "purple", 8991416, MapColor.PURPLE, 8073150, 10494192);
    public static final DyeColor BLUE = registryDyeColorVanilla( "blue", 3949738, MapColor.BLUE, 2437522, 255);
    public static final DyeColor BROWN = registryDyeColorVanilla( "brown", 8606770, MapColor.BROWN, 5320730, 9127187);
    public static final DyeColor GREEN = registryDyeColorVanilla( "green", 6192150, MapColor.GREEN, 3887386, 65280);
    public static final DyeColor RED = registryDyeColorVanilla( "red", 11546150, MapColor.RED, 11743532, 16711680);
    public static final DyeColor BLACK = registryDyeColorVanilla( "black", 1908001, MapColor.BLACK, 1973019, 0);

    public static DyeColor registryDyeColor(Identifier id, int baseColor, MapColor mapColor){
        return registryDyeColorNameOverride(id, id.getPath(), baseColor, mapColor);
    }

    public static DyeColor registryDyeColorNameOverride(Identifier id, String overrideDisplayName, int baseColor, MapColor mapColor){
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
    private static DyeColor registryDyeColorVanilla(String dyeName, int baseColor, MapColor mapColor, int fireworkColor, int signColor){
        DyeColor dyeColor = registryDyeColor(new Identifier(dyeName), dyeName, mapColor, baseColor, fireworkColor, signColor);

        DYE_COLOR_INT_MAP.add(new Identifier(dyeName));
        VANILLA_DYES.add(dyeColor);

        return dyeColor;
    }

    public static DyeColor registryDyeColor(Identifier id, String displayName, MapColor mapColor, int baseColor, int fireworkColor, int signColor){
        DyeColor dyeColor = new DyeColor(displayName, ColorUtil.getColorComponents(baseColor),  mapColor, baseColor, fireworkColor, signColor);

        BY_FIREWORK_COLOR.put(fireworkColor, dyeColor);
        return Registry.register(DYE_COLOR, id, dyeColor);
    }

    public record DyeColor(String displayName, float[] colorComponents, MapColor mapColor, int baseColor, int fireworkColor, int signColor){

        public Identifier getId() {
            return DyeColorRegistry.DYE_COLOR.getId(this);
        }

        public String getName() {
            return getId().getPath();
        }

        /**
         * Returns the red, blue and green components of this dye color.
         *
         * @return an array composed of the red, blue and green floats
         */
        public float[] getColorComponents() {
            return this.colorComponents;
        }

        public MapColor getMapColor() {
            return this.mapColor;
        }

        public int getFireworkColor() {
            return this.fireworkColor;
        }

        public int getSignColor() {
            return this.signColor;
        }

        public static DyeColor byId(Identifier id) {
            return DyeColorRegistry.DYE_COLOR.get(id);
        }

        public static DyeColor byOldDyeId(int id) {
            return byId(DYE_COLOR_INT_MAP.get(id));
        }

        public static DyeColor byName(String name, DyeColor defaultColor) {
            for(DyeColor dyeColor : DYE_COLOR.stream().toList()) {
                if (dyeColor.getName().equals(name)) {
                    return dyeColor;
                }
            }

            return defaultColor;
        }

        @Nullable
        public static DyeColor byFireworkColor(int color) {
            return BY_FIREWORK_COLOR.get(color);
        }

        public String toString() {
            return this.getName();
        }

        public String asString() {
            return this.getName();
        }

        public static DyeColor byOldDyeColor(net.minecraft.util.DyeColor dyeColor) {
            return byOldDyeId(dyeColor.getId());
        }

        public String getDisplayName(){
            return this.displayName;
        }
    }

    //------------------------------------------------------------------------------------


}
