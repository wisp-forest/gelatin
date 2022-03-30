package io.wispforest.jello.api.util;

import io.wispforest.jello.api.dye.DyeColorant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.util.List;

public class ColorUtil {


    public static float[] getCMYFromIntColor(int baseColor){
        Color rgb = new Color(baseColor);

        return rgb.getColorComponents(ColorSpace.getInstance(ColorSpace.TYPE_CMY), null);
    }

    public static int getIntColorFromCMY(float[] cmy){
        return new Color(ColorSpace.getInstance(ColorSpace.TYPE_CMY), cmy, 1).getRGB();
    }

//    public static int getDecimalColor(int R, int G, int B, int A){
//        Color color = Color.ofRGBA(R, G, B, A);
//        return color.getColor();
//    }
//
//    public static int getDecimalColor(float R, float G, float B, float A){
//        Color color = Color.ofRGBA(R, G, B, A);
//        return color.getColor();
//    }
//
//    public static Color getColor(int color){
//        return Color.ofTransparent(color);
//    }

    public static float[] rainbowColorizer(LivingEntity livingEntity) {
        float tickDelta = MinecraftClient.getInstance().getTickDelta();
        return rainbowColorizer(livingEntity, tickDelta);
    }

    public static float[] getColorComponents(int baseColor){
        int j = (baseColor & 0xFF0000) >> 16;
        int k = (baseColor & 0xFF00) >> 8;
        int l = (baseColor & 0xFF);
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


    public static int blendDyeColors(DyeColorant... colors) {
        int[] is = new int[3];
        int i = 0;
        int j = 0;

        for(DyeColorant dyeColorant : colors) {
            float[] fs = dyeColorant.getColorComponents();
            int l = (int)(fs[0] * 255.0F);
            int m = (int)(fs[1] * 255.0F);
            int n = (int)(fs[2] * 255.0F);
            i += Math.max(l, Math.max(m, n));
            is[0] += l;
            is[1] += m;
            is[2] += n;
            ++j;
        }

        int k = is[0] / j;
        int o = is[1] / j;
        int p = is[2] / j;
        float h = (float)i / (float)j;
        float q = (float)Math.max(k, Math.max(o, p));
        k = (int)((float)k * h / q);
        o = (int)((float)o * h / q);
        p = (int)((float)p * h / q);
        int var26 = (k << 8) + o;
        var26 = (var26 << 8) + p;

        return var26;
    }

    public static ItemStack blendItemColorAndDyeColor(ItemStack stack, List<DyeColorant> colors) {
        ItemStack itemStack = ItemStack.EMPTY;
        int[] is = new int[3];
        int i = 0;
        int j = 0;
        DyeableItem dyeableItem = null;
        Item item = stack.getItem();
        if (item instanceof DyeableItem) {
            dyeableItem = (DyeableItem)item;
            itemStack = stack.copy();
            itemStack.setCount(1);
            if (dyeableItem.hasColor(stack)) {
                int k = dyeableItem.getColor(itemStack);
                float f = (float)(k >> 16 & 0xFF) / 255.0F;
                float g = (float)(k >> 8 & 0xFF) / 255.0F;
                float h = (float)(k & 0xFF) / 255.0F;
                i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0F);
                is[0] = (int)((float)is[0] + f * 255.0F);
                is[1] = (int)((float)is[1] + g * 255.0F);
                is[2] = (int)((float)is[2] + h * 255.0F);
                ++j;
            }

            for(DyeColorant dyeColorant : colors) {
                float[] fs = dyeColorant.getColorComponents();
                int l = (int)(fs[0] * 255.0F);
                int m = (int)(fs[1] * 255.0F);
                int n = (int)(fs[2] * 255.0F);
                i += Math.max(l, Math.max(m, n));
                is[0] += l;
                is[1] += m;
                is[2] += n;
                ++j;
            }
        }

        if (dyeableItem == null) {
            return ItemStack.EMPTY;
        } else {
            int k = is[0] / j;
            int o = is[1] / j;
            int p = is[2] / j;
            float h = (float)i / (float)j;
            float q = (float)Math.max(k, Math.max(o, p));
            k = (int)((float)k * h / q);
            o = (int)((float)o * h / q);
            p = (int)((float)p * h / q);
            int var26 = (k << 8) + o;
            var26 = (var26 << 8) + p;
            dyeableItem.setColor(itemStack, var26);
            return itemStack;
        }
    }








}
