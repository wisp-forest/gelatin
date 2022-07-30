package io.wispforest.jello.api.registry;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.util.GrayScaleHelper;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

@ApiStatus.Experimental
public class GrayScaleBlockRegistry implements GrayScaleHelper<Block> {

    public static final Set<Identifier> GRAYSCALABLE_BLOCK_SPRITES = new HashSet<>();

    public static GrayScaleBlockRegistry INSTANCE = new GrayScaleBlockRegistry();

    public static void register(Block block){
        register(Registry.BLOCK.getId(block));
    }

    public static void register(Identifier id){
        GRAYSCALABLE_BLOCK_SPRITES.add(new Identifier(id.getNamespace(), "block/" + id.getPath()));
    }

    @Override
    public String getGrayscaleSuffix() {
        return "_gray";
    }

    @Override
    public Identifier createGrayScaleID(Identifier defaultIdentifier) {
        return new Identifier(Jello.MODID, defaultIdentifier.getPath() + getGrayscaleSuffix());
    }
}
