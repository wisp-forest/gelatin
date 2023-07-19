package io.wispforest.gelatin.dye_entities.ducks;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * Interface used to allow for Custom Color or Rainbow effect for a pets collar
 */
public interface CollarColorable {

    DyeColorant getCustomCollarColor();

    boolean setCustomCollarColor(DyeColorant dyeColorant);

    boolean isRainbowCollared();

    boolean setRainbowCollar(boolean rainbowCollarMode);

    //------

    static void setDefaultValues(CollarColorable collarColorable){
        collarColorable.setCustomCollarColor(DyeColorantRegistry.NULL_VALUE_NEW);
        collarColorable.setRainbowCollar(false);
    }

    static void readNbtData(CollarColorable collarColorable, NbtCompound nbt){
        collarColorable.setCustomCollarColor(DyeColorant.byId(Identifier.tryParse(nbt.getString("CustomCollarColor"))));
        collarColorable.setRainbowCollar(nbt.getBoolean("RainbowCollar"));
    }

    static void writeNbtData(CollarColorable collarColorable, NbtCompound nbt){
        nbt.putString("CustomCollarColor", collarColorable.getCustomCollarColor().getId().toString());
        nbt.putBoolean("RainbowCollar", collarColorable.isRainbowCollared());
    }
}
