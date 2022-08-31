package io.wispforest.gelatin.dye_entities.ducks;

import io.wispforest.gelatin.common.CommonInit;
import io.wispforest.gelatin.dye_entities.mixins.EnchantedGoldenAppleMixin;
import io.wispforest.gelatin.dye_entities.client.utils.GrayScaleEntityRegistry;
import net.minecraft.entity.Entity;

/**
 * RainbowEntity Interface is used to give your entity a Rainbow effect like the _Jeb easter Egg.
 * Only Implement this interface if you want to change how the Rainbow Effect is applyed
 * <br><br>
 * e.g. If you want your entity to default rainbow, change the {@link #isRainbowTime()} such that it only returns true
 */
public interface RainbowEntity extends GrayScaleEntity {

    /**
     * A method used to tell if A entity will be rendered with the Rainbow Effect similar to _Jeb
     */
    default boolean isRainbowTime() {
        return rainbowOverride();
    }

    /**
     * Used by the {@link EnchantedGoldenAppleMixin} to disable rainbow event,
     *
     * @param value boolean value for either turning on or off rainbow
     */
    default void setRainbowTime(boolean value) {}

    /**
     * A method used to forever have Rainbow Effect Enabled
     */
    default boolean rainbowOverride() {
        return false;
    }

    //--------------------

    @Override
    default boolean isGrayScaled(Entity entity) {
        return CommonInit.getConfig().enableGrayScaleRainbowEntities() && (isRainbowTime() && !GrayScaleEntityRegistry.isBlacklisted(entity));
    }

}
