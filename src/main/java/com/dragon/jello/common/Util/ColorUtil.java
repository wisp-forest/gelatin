package com.dragon.jello.common.Util;

import me.shedaniel.math.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class ColorUtil {

    public static int getDecimalColor(int R, int G, int B, int A){
        Color color = Color.ofRGBA(R, G, B, A);
        return color.getColor();
    }

    public static int getDecimalColor(float R, float G, float B, float A){
        Color color = Color.ofRGBA(R, G, B, A);
        return color.getColor();
    }

    public static Color getColor(int color){
        return Color.ofTransparent(color);
    }

    public static float[] rainbowColorizer(LivingEntity livingEntity) {
        float tickDelta = MinecraftClient.getInstance().getTickDelta();
        return rainbowColorizer(livingEntity, tickDelta);
    }

    public static float[] getColorComponents(int baseColor){
        int j = (baseColor & 0xFF0000) >> 16;
        int k = (baseColor & 0xFF00) >> 8;
        int l = (baseColor & 0xFF) >> 0;
        return new float[]{(float)j / 255.0F, (float)k / 255.0F, (float)l / 255.0F};
    }

    public static float[] rainbowColorizer(LivingEntity livingEntity, float g){
        int n = livingEntity.age / 25 + livingEntity.getId();
        int dye = DyeColor.values().length;
        int p = n % dye;
        int q = (n + 1) % dye;
        float r = ((float) (livingEntity.age % 25) + g) / 25.0F;
        float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
        float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));

        float r1 = fs[0] * (1.0F - r) + gs[0] * r;
        float g1 = fs[1] * (1.0F - r) + gs[1] * r;
        float b1 = fs[2] * (1.0F - r) + gs[2] * r;

        return new float[]{r1, g1, b1};
    }








}
