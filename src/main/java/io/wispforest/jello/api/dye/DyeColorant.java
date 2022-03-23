package io.wispforest.jello.api.dye;

import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.util.ColorUtil;
import net.minecraft.block.MapColor;
import net.minecraft.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;

public class DyeColorant {
    
    private final RegistryEntry.Reference<DyeColorant> registryEntry = DyeColorantRegistry.DYE_COLOR.createEntry(this);

    private final String displayName;
    private final MapColor mapColor;
    private final float[] colorComponents;

    private final int baseColor;
    private final int fireworkColor;
    private final int signColor;

    public DyeColorant(String displayName, MapColor mapColor, int baseColor, int fireworkColor, int signColor){
        this.displayName = displayName;
        this.colorComponents = ColorUtil.getColorComponents(baseColor);
        this.mapColor = mapColor;
        this.baseColor = baseColor;
        this.fireworkColor = fireworkColor;
        this.signColor = signColor;
    }

    //----------------------------------------------------------------

    public Identifier getId() {
        return DyeColorantRegistry.DYE_COLOR.getId(this);
    }

    public String getName() {
        return getId().getPath();
    }

    //----------------------------------------------------------------

    public int getBaseColor(){
        return this.baseColor;
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

    //----------------------------------------------------------------

    public String toString() {
        return this.getName();
    }

    public String asString() {
        return this.getName() + " / " + getBaseColor();
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public RegistryEntry.Reference<DyeColorant> getRegistryEntry() {
        return this.registryEntry;
    }

    //----------------------------------------------------------------

    public boolean isIn(TagKey<DyeColorant> tag) {
        return getRegistryEntry().isIn(tag);
    }

    public static DyeColorant byId(Identifier id) {
        return DyeColorantRegistry.DYE_COLOR.get(id);
    }

    public static DyeColorant byName(String name, DyeColorant defaultColor) {
        for(DyeColorant dyeColor : DyeColorantRegistry.DYE_COLOR.stream().toList()) {
            if (dyeColor.getName().equals(name)) {
                return dyeColor;
            }
        }

        return defaultColor;
    }

    public static DyeColorant byOldIntId(int id) {
        DyeColorant dyeColor = DyeColorantRegistry.Constants.VANILLA_DYES.get(id);

        return dyeColor != null ? dyeColor : DyeColorantRegistry.Constants.NULL_VALUE_NEW;
    }

    public static DyeColorant byFireworkColor(int color) {
        for(DyeColorant dyeColor : DyeColorantRegistry.DYE_COLOR.stream().toList()) {
            if (dyeColor.getFireworkColor() == color) {
                return dyeColor;
            }
        }

        return null;
    }

    public static DyeColorant byOldDyeColor(DyeColor dyeColor) {
        return byOldIntId(dyeColor.getId());
    }

    //----------------------------------------------------------------
}
