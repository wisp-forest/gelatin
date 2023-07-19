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

    public static final TrackedData<Byte> RAINBOW_MODE = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Integer> COLOR_VALUE = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final List<String> NBT_CONSTANTS = new ArrayList<>(Arrays.asList("BaseDyeColor", "RainbowMode", "ConstantColor", "ColoredValue"));

    public static String getDyeColorNbtKey() {
        return NBT_CONSTANTS.get(0);
    }

    public static String getRainbowNbtKey() {
        return NBT_CONSTANTS.get(1);
    }

    public static String getConstantColorNbtKey() {
        return NBT_CONSTANTS.get(2);
    }

    public static String getColoredNbtkey() {
        return NBT_CONSTANTS.get(3);
    }

}
