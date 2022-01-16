package com.dragon.jello.Util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayDeque;
import java.util.Deque;

public class ColorStateManager {

    public static final Deque<Boolean> GRAY_SCALE_TEST = new ArrayDeque<>();

    public static void enableGrayScale() {
        RenderSystem.assertOnRenderThread();
        GRAY_SCALE_TEST.add(true);
    }

    public static void disableGrayScale() {
        RenderSystem.assertOnRenderThread();
        GRAY_SCALE_TEST.add(false);
    }

    public static boolean isGrayScaleEnabled(){
        RenderSystem.assertOnRenderThread();

        if(!GRAY_SCALE_TEST.isEmpty()){
            return Boolean.TRUE.equals(GRAY_SCALE_TEST.peekLast());
        }

        return false;
    }
}
