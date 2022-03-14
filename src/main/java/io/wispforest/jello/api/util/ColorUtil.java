package io.wispforest.jello.api.util;

import me.shedaniel.math.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;

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

    /**
     *  Note: Code was based off of/used from <a href="https://chir.ag/projects/ntc/ntc.js">ntc.js</a>, created by Chirag Mehta,
     *  under the <a href="http://creativecommons.org/licenses/by/2.5/">Creative Commons Licences</a>
     */
    public static float[] getHSLfromColor(int color) {
        var rgb = new float[]{(color >> 16)/ 255F, ((color >> 8) & 0xFF)/ 255F, (color & 0xFF)/ 255F};

        var r = rgb[0];
        var g = rgb[1];
        var b = rgb[2];

        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));
        float delta = max - min;
        float l = (min + max) / 2;

        float s = 0;
        if(l > 0 && l < 1)
            s = delta / (l < 0.5 ? (2 * l) : (2 - 2 * l));

        float h = 0;
        if(delta > 0)
        {
            if (max == r && max != g) h += (g - b) / delta;
            if (max == g && max != b) h += (2 + (b - r) / delta);
            if (max == b && max != r) h += (4 + (r - g) / delta);
            h /= 6;
        }

        return new float[]{(int) (h * 255F), (int) (s * 255F), (int) (l * 255F)};
    }








}
