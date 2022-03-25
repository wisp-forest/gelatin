package io.wispforest.jello.api.dye.registry.builder;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyedVariants;
import io.wispforest.jello.api.registry.ColorBlockRegistry;

import java.util.ArrayList;
import java.util.List;

public class BaseBlockBuilder {

    public static final List<BaseBlockBuilder> ADDITIONAL_BUILDERS = new ArrayList<>();

    protected final List<BlockType> blockTypes;
    protected final BlockBuildFunction baseBlockBuilder;
    public final String modid;

    public BaseBlockBuilder(String modid, List<BlockType> blockTypes, BlockBuildFunction baseBlockBuilder){
        this.modid = modid;
        this.blockTypes = blockTypes;
        this.baseBlockBuilder = baseBlockBuilder;
    }

    public List<BlockType> getBlockTypes(){
        return this.blockTypes;
    }

    public List<BlockType.RegistryHelper> build(DyeColorant dyeColorant){
        return this.baseBlockBuilder.createMultiBlockSet(blockTypes, dyeColorant, false);
    }

    public static void registerAdditionalBlockBuilder(BaseBlockBuilder baseBlockBuilder){
        ADDITIONAL_BUILDERS.add(baseBlockBuilder);

        DyedVariants.addToAlreadyExistingVariants(baseBlockBuilder);
        ColorBlockRegistry.registerBlockType(baseBlockBuilder.blockTypes);
    }

    public interface BlockBuildFunction {
        List<BlockType.RegistryHelper> createMultiBlockSet(List<BlockType> blockTypes, DyeColorant dyeColorant, boolean readOnly);
    }

}
