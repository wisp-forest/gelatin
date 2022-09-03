package io.wispforest.gelatin.dye_entities.ducks;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public interface CustomCollarColorStorage {

    DyeColorant getCustomCollarColor();

    void setCustomCollarColor(DyeColorant dyeColorant);

    boolean isRainbowCollared();

    void setRainbowCollar(boolean rainbowCollarMode);

    default void setDefaultValues(){
        this.setCustomCollarColor(DyeColorantRegistry.NULL_VALUE_NEW);
        this.setRainbowCollar(false);
    }

    default void readNbtData(NbtCompound nbt){
        this.setCustomCollarColor(DyeColorant.byId(Identifier.tryParse(nbt.getString("CustomCollarColor"))));
        this.setRainbowCollar(nbt.getBoolean("RainbowCollar"));
    }

    default void writeNbtData(NbtCompound nbt){
        nbt.putString("CustomCollarColor", this.getCustomCollarColor().getId().toString());
        nbt.putBoolean("RainbowCollar", this.isRainbowCollared());
    }

}
