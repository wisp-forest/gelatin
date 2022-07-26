package io.wispforest.jello.api.util;

import io.wispforest.jello.misc.JelloConstants;
import net.minecraft.util.Identifier;

public interface GrayScaleHelper<T> {

    String getGrayscaleSuffix();

    default Identifier createGrayScaleID(Identifier defaultIdentifier) {
        String[] array = defaultIdentifier.getPath().split("/");
        String[] array2 = array[array.length - 1].split("\\.");

        String path = array[0];

        for (int i = 1; i < array.length - 1; i++) {
            path = path.concat("/" + array[i]);
        }

        path = path + "/" + array2[0] + getGrayscaleSuffix();

        return JelloConstants.id(path);
    }
}
