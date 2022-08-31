package io.wispforest.gelatin.dye_entities.misc;

import io.wispforest.gelatin.common.util.TrackedDataHandlerExtended;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataConstants {

    public static final TrackedData<Identifier> DYE_COLOR = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerExtended.IDENTIFIER);
    public static final TrackedData<Byte> RAINBOW_MODE = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Integer> CONSTANT_COLOR = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final int DEFAULT_NULL_COLOR_VALUE = 0x1000000;

    private static final List<String> NBT_CONSTANTS = new ArrayList<>(Arrays.asList("BaseDyeColor", "RainbowMode", "ConstantColor"));

    public static String getDyeColorNbtKey() {
        return NBT_CONSTANTS.get(0);
    }

    public static String getRainbowNbtKey() {
        return NBT_CONSTANTS.get(1);
    }

    public static String getConstantColorNbtKey() {
        return NBT_CONSTANTS.get(2);
    }

}
