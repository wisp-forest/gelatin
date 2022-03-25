package io.wispforest.jello.main.common.compat;

import io.wispforest.jello.api.dye.block.ColoredBlock;
import io.wispforest.jello.api.dye.block.ColoredConcretePowderBlock;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.builder.BaseBlockBuilder;
import io.wispforest.jello.api.dye.registry.builder.BlockType;
import io.wispforest.jello.api.dye.registry.builder.VanillaBlockBuilder;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.common.blocks.SlimeBlockColored;
import io.wispforest.jello.main.common.blocks.SlimeSlabColored;
import io.wispforest.jello.main.common.compat.owo.BaseBlockBuilderContainer;
import io.wispforest.jello.main.common.data.tags.JelloTags;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class JelloBlockBuilders implements BaseBlockBuilderContainer {

    public static final BaseBlockBuilder SLIME_VARS = new BaseBlockBuilder("jello", new BlockType.Builder().of("slime_block", new Identifier("slime_block"), JelloTags.Blocks.COLORED_SLIME_BLOCKS, JelloTags.Items.SLIME_BLOCKS, ItemGroup.REDSTONE).of("slime_slab", new Identifier(Jello.MODID, "slime_slab"), JelloTags.Blocks.COLORED_SLIME_SLABS, JelloTags.Items.SLIME_SLABS, ItemGroup.REDSTONE).getTypes(),
        (blockTypes, dyeColorant, readOnly) -> {
            List<BlockType.RegistryHelper> CURRENT_SET = new ArrayList<>();

            Block block = new SlimeBlockColored(dyeColorant, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
            CURRENT_SET.add(BlockType.createHelper(blockTypes.get(0), block));

            Block block2 = new SlimeSlabColored(dyeColorant, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
            CURRENT_SET.add(BlockType.createHelper(blockTypes.get(1), block2));

            return CURRENT_SET;
        });
}
