package io.wispforest.dye_entries.utils;

import io.wispforest.common.misc.GelatinConstants;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

@ApiStatus.Experimental
public class GrayScaleBlockRegistry  {

    public static final Set<Identifier> GRAYSCALABLE_BLOCK_SPRITES = new HashSet<>();

    public static GrayScaleBlockRegistry INSTANCE = new GrayScaleBlockRegistry();

    public static void register(Block block){
        register(Registry.BLOCK.getId(block));
    }

    public static void register(Identifier id){
        GRAYSCALABLE_BLOCK_SPRITES.add(new Identifier(id.getNamespace(), "block/" + id.getPath()));
    }

    public Identifier createGrayScaleID(Identifier defaultIdentifier) {
        return new Identifier(GelatinConstants.MODID, defaultIdentifier.getPath() + "_gray");
    }
}
