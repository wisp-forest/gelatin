package io.wispforest.jello.misc;

import io.wispforest.jello.Jello;
import net.minecraft.util.Identifier;

public class JelloConstants {

    public static Identifier id(String path) {
        return new Identifier(Jello.MODID, path);
    }
}
