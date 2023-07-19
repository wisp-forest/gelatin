package io.wispforest.gelatin.dye_entities.misc;

import io.wispforest.gelatin.dye_entities.ducks.Colorable;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;

public class EntityColorImplementations {
    /**
     * Event used when an {@link Colorable} will Change its color based upon the given {@link DyeColorant}
     *
     * @param colorable Entity to be Dyed
     * @param dyeColor Color to be applyed to the Entity
     * @return True if the Entity's color was changed
     */
    public static boolean dyeEntityEvent(Colorable colorable, DyeColorant dyeColor) {
        return !colorable.isRainbow() && colorable.setColor(dyeColor.getBaseColor());
    }

    /**
     * Event used when an {@link Colorable} is to be Rainbowed
     *
     * @param colorable Entity to be Dyed
     * @return True if the Entity's was Raindowed
     */
    public static boolean rainbowEntityEvent(Colorable colorable) {
        return !colorable.isColored() && colorable.setRainbow(true);
    }

    /**
     * Event used when an {@link Colorable} is to be DeColored or De-Rainbowed
     *
     * @param colorable Entity to be cleaned
     * @return True if the Entity's was Cleaned
     */
    public static boolean washEntityEvent(Colorable colorable) {
        return colorable.setColor(DyeColorantRegistry.NULL_VALUE_NEW.getBaseColor()) || colorable.setRainbow(false);
    }
}
