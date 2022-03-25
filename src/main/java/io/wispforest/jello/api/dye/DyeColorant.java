package io.wispforest.jello.api.dye;

import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.util.ColorUtil;
import net.minecraft.block.MapColor;
import net.minecraft.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import org.jetbrains.annotations.ApiStatus;

public class DyeColorant {
    
    private final RegistryEntry.Reference<DyeColorant> registryEntry = DyeColorantRegistry.DYE_COLOR.createEntry(this);

    private final MapColor mapColor;
    private final float[] colorComponents;

    private final int baseColor;
    private final int fireworkColor;
    private final int signColor;

    public DyeColorant(MapColor mapColor, int baseColor, int fireworkColor, int signColor){
        this.colorComponents = ColorUtil.getColorComponents(baseColor);
        this.mapColor = mapColor;
        this.baseColor = baseColor;
        this.fireworkColor = fireworkColor;
        this.signColor = signColor;
    }

    //----------------------------------------------------------------

    /**
     * @return the {@link Identifier} of the {@link DyeColorant} from the {@link DyeColorantRegistry#DYE_COLOR} Registry
     */
    public Identifier getId() {
        return DyeColorantRegistry.DYE_COLOR.getId(this);
    }

    /**
     * @return the path or name of the {@link DyeColorant} from the {@link DyeColorantRegistry#DYE_COLOR} Registry
     */
    public String getName() {
        return getId().getPath();
    }

    //----------------------------------------------------------------

    /**
     * @return the Integer color value given to the DyeColor
     */
    public int getBaseColor(){
        return this.baseColor;
    }

    /**
     * @return the red, blue and green components of this dye color.
     */
    public float[] getColorComponents() {
        return this.colorComponents;
    }

    public MapColor getMapColor() {
        return this.mapColor;
    }

    //TODO: Implement Custom Firework Colors
    public int getFireworkColor() {
        return this.fireworkColor;
    }

    //TODO: Implement Custom Sign Colors
    public int getSignColor() {
        return this.signColor;
    }

    //----------------------------------------------------------------

    public RegistryEntry.Reference<DyeColorant> getRegistryEntry() {
        return this.registryEntry;
    }

    /**
     * Checks if the {@link DyeColorant} is within the given tag.
     *
     * @param tag Key of the tag being checked
     * @return True or False depending on if it is within the given tag
     */
    public boolean isIn(TagKey<DyeColorant> tag) {
        return getRegistryEntry().isIn(tag);
    }

    /**
     * Gets a {@link DyeColorant} if the given {@link Identifier} has been registered.
     *
     * @param id The registered {@link Identifier} for a {@link DyeColorant}
     * @return {@link DyeColorant}
     */
    public static DyeColorant byId(Identifier id) {
        return DyeColorantRegistry.DYE_COLOR.get(id);
    }

    //----------------------------------------------------------------

    public String toString() {
        return this.getName();
    }

    @ApiStatus.Internal
    public static DyeColorant byName(String name, DyeColorant defaultColor) {
        for(DyeColorant dyeColor : DyeColorantRegistry.DYE_COLOR.stream().toList()) {
            if (dyeColor.getName().equals(name)) {
                return dyeColor;
            }
        }

        return defaultColor;
    }

    @ApiStatus.Internal
    public static DyeColorant byOldIntId(int id) {
        DyeColorant dyeColor = DyeColorantRegistry.Constants.VANILLA_DYES.get(id);

        return dyeColor != null ? dyeColor : DyeColorantRegistry.NULL_VALUE_NEW;
    }

    @ApiStatus.Internal
    public static DyeColorant byFireworkColor(int color) {
        for(DyeColorant dyeColor : DyeColorantRegistry.DYE_COLOR.stream().toList()) {
            if (dyeColor.getFireworkColor() == color) {
                return dyeColor;
            }
        }

        return null;
    }

    @ApiStatus.Internal
    public static DyeColorant byOldDyeColor(DyeColor dyeColor) {
        return byOldIntId(dyeColor.getId());
    }

    //----------------------------------------------------------------
}
