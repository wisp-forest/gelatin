package io.wispforest.dye_entities.misc;

import io.wispforest.dye_entities.ducks.DyeableEntity;
import io.wispforest.dye_entities.ducks.RainbowEntity;
import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;

public class EntityColorManipulators {
    /**
     * Event used when an {@link DyeableEntity} will Change its color based upon the given {@link DyeColorant}
     *
     * @param dyeableEntity Entity to be Dyed
     * @param dyeColor Color to be applyed to the Entity
     * @return True if the Entity's color was changed
     */
    public static boolean dyeEntityEvent(DyeableEntity dyeableEntity, DyeColorant dyeColor) {
        if ((dyeableEntity.isRainbowTime()) || dyeColor == dyeableEntity.getDyeColor()) {//|| dyeItem.getColor().getId() == DyeColor.WHITE.getId()) {
            return false;
        }

        dyeableEntity.setDyeColor(dyeColor);

        return true;
    }

    /**
     * Event used when an {@link RainbowEntity} is to be Rainbowed
     *
     * @param rainbowEntity Entity to be Dyed
     * @return True if the Entity's was Raindowed
     */
    public static boolean rainbowEntityEvent(RainbowEntity rainbowEntity) {
        if (rainbowEntity.isRainbowTime() || (rainbowEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed())) {
            return false;
        }

        rainbowEntity.setRainbowTime(true);

        return true;
    }

    /**
     * Event used when an {@link DyeableEntity} is to be DeColored or De-Rainbowed
     *
     * @param dyeableEntity Entity to be cleaned
     * @return True if the Entity's was Cleaned
     */
    public static boolean washEntityEvent(DyeableEntity dyeableEntity) {
        boolean washedEntity = false;

        if (dyeableEntity.isDyed()) {
            dyeableEntity.setDyeColor(DyeColorantRegistry.NULL_VALUE_NEW);
            washedEntity = true;
        }

        if (dyeableEntity.isRainbowTime()) {
            dyeableEntity.setRainbowTime(false);
            washedEntity = true;
        }

        return washedEntity;
    }
}
