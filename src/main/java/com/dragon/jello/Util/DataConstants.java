package com.dragon.jello.Util;

import com.dragon.jello.mixin.mixins.LivingEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

import java.util.*;

public class DataConstants {

    private static final List<String> NBT_CONSTANTS = new ArrayList<>(Arrays.asList("EntityColor", "GrayScaleMode", "RainbowMode"));

    public static String getDyeColorNbtKey(){
        return NBT_CONSTANTS.get(0);
    }

    public static String getGrayScaleNbtKey(){
        return NBT_CONSTANTS.get(1);
    }

    public static String getRainbowNbtKey(){
        return NBT_CONSTANTS.get(2);
    }

}
