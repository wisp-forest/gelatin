package io.wispforest.common.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.nio.IntBuffer;

public class ColorUtil {

    public static float[] rgbToCmy(int baseColor) {
        Color rgb = new Color(baseColor);

        return rgb.getColorComponents(ColorSpace.getInstance(ColorSpace.TYPE_CMY), null);
    }

    public static int cmyToRgb(float[] cmy) {
        return new Color(ColorSpace.getInstance(ColorSpace.TYPE_CMY), cmy, 1).getRGB();
    }

    public static float[] rainbowColorizer(LivingEntity livingEntity) {
        return rainbowColorizer(livingEntity, MinecraftClient.getInstance().getTickDelta());
    }

    public static float[] getColorComponents(int baseColor) {
        int j = (baseColor & 0xFF0000) >> 16;
        int k = (baseColor & 0xFF00) >> 8;
        int l = (baseColor & 0xFF);
        return new float[]{(float) j / 255.0F, (float) k / 255.0F, (float) l / 255.0F};
    }

    public static float[] rainbowColorizer(LivingEntity livingEntity, float tickDelta) {
        int n = livingEntity.age / 25 + livingEntity.getId();
        int dye = DyeColor.values().length;
        int p = n % dye;
        int q = (n + 1) % dye;
        float r = ((float) (livingEntity.age % 25) + tickDelta) / 25.0F;
        float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
        float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));

        float r1 = fs[0] * (1.0F - r) + gs[0] * r;
        float g1 = fs[1] * (1.0F - r) + gs[1] * r;
        float b1 = fs[2] * (1.0F - r) + gs[2] * r;

        return new float[]{r1, g1, b1};
    }

    /**
     * Note: Code was based off of/used from <a href="https://chir.ag/projects/ntc/ntc.js">ntc.js</a>, created by Chirag Mehta,
     * under the <a href="http://creativecommons.org/licenses/by/2.5/">Creative Commons Licences</a>
     */
    public static float[] rgbToHsl(int color) {
        var rgb = new float[]{(color >> 16) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F};

        var r = rgb[0];
        var g = rgb[1];
        var b = rgb[2];

        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));
        float delta = max - min;
        float l = (min + max) / 2;

        float s = 0;
        if (l > 0 && l < 1)
            s = delta / (l < 0.5 ? (2 * l) : (2 - 2 * l));

        float h = 0;
        if (delta > 0) {
            if (max == r && max != g) h += (g - b) / delta;
            if (max == g && max != b) h += (2 + (b - r) / delta);
            if (max == b && max != r) h += (4 + (r - g) / delta);
            h /= 6;
        }

        return new float[]{(int) (h * 255F), (int) (s * 255F), (int) (l * 255F)};
    }

    public static double luminance(Color color) {
        return luminance(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static double luminance(float[] colorComp) {
        return luminance((int) (colorComp[0] * 255.0F), (int) (colorComp[1] * 255.0F), (int) (colorComp[2] * 255.0F));
    }

    public static double luminance(int r, int g, int b) {
        if (r == g && r == b) return r;

        return 0.299 * r + 0.587 * g + 0.114 * b;
    }

    public static int toGray(double luminance) {
        return (int) (Math.round(luminance));
    }

    public static Color toGray(Color color) {
        int y = (int) (Math.round(luminance(color)));

        return new Color(y, y, y);
    }

    public static NativeImage convertImageToGrayScale(NativeImage nativeImageOrigin){
        NativeImage imageCopy = new NativeImage(nativeImageOrigin.getWidth(), nativeImageOrigin.getHeight(),false);
        imageCopy.copyFrom(nativeImageOrigin);

        long pointer = imageCopy.pointer;

        final IntBuffer buffer = MemoryUtil.memIntBuffer(pointer, (imageCopy.getWidth() * imageCopy.getHeight()));

        int[] pixelColors = new int[buffer.remaining()];

        buffer.get(pixelColors);
        buffer.clear();

        for (int index = 0; index < pixelColors.length; index++) {
            int currentColor = pixelColors[index];
            int a1 = (currentColor >> 24) & 0xff;
            if(a1 != 0){
                int r1 = (currentColor >> 16) & 0xFF;
                int g1 = (currentColor >> 8) & 0xFF;
                int b1 = (currentColor >> 0) & 0xFF;

                //int grayValue = (r1 + g1 + b1) / 3;

                int grayValue = ColorUtil.toGray(ColorUtil.luminance(r1, g1, b1));

                pixelColors[index] = ((a1 & 0xFF) << 24) | ((grayValue & 0xFF) << 16) | ((grayValue & 0xFF) << 8)  | ((grayValue & 0xFF) << 0);
            }
        }

        buffer.put(pixelColors);
        buffer.clear();

        return imageCopy;
    }
}
