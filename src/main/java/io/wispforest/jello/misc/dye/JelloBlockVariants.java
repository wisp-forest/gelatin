package io.wispforest.jello.misc.dye;

import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import io.wispforest.jello.Jello;
import io.wispforest.jello.block.SlimeBlockColored;
import io.wispforest.jello.block.SlimeSlabColored;
import io.wispforest.jello.data.loot.JelloLootTables;
import io.wispforest.jello.data.tags.JelloTags;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class JelloBlockVariants {

    public static final DyeableBlockVariant SLIME_BLOCK = DyeableBlockVariant.of(Jello.id("slime_block"), ItemGroup.REDSTONE, (dyeColorant, parentBlock) -> {
        return new SlimeBlockColored(dyeColorant, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
    }).setDefaultBlock(new Identifier("slime_block"))
            .setBlockTags(JelloTags.Blocks.COLORED_SLIME_BLOCKS, JelloTags.Blocks.SLIME_BLOCKS, JelloTags.Blocks.STICKY_BLOCKS)
            .register();

    public static final DyeableBlockVariant SLIME_SLAB = DyeableBlockVariant.of(Jello.id("slime_slab"), ItemGroup.REDSTONE, (dyeColorant, parentBlock) -> {
        return new SlimeSlabColored(dyeColorant, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
    }).setDefaultBlock("slime_slab")
            .setBlockTags(JelloTags.Blocks.COLORED_SLIME_SLABS, JelloTags.Blocks.SLIME_SLABS, JelloTags.Blocks.STICKY_BLOCKS)
            .setLootTable(block -> JelloLootTables.slabDrops(block).build())
            .register();

    public static void initialize() {}
}
