package io.wispforest.gelatin.common.util;

import net.minecraft.client.texture.NativeImage;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class GrayScaleUtils {

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
