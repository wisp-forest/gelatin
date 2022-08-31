package io.wispforest.jello.misc;

import io.wispforest.dye_entries.data.GelatinLootTables;
import io.wispforest.dye_entries.item.ColoredBlockItem;
import io.wispforest.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.jello.Jello;
import io.wispforest.jello.block.SlimeBlockColored;
import io.wispforest.jello.block.SlimeSlabColored;
import io.wispforest.jello.data.JelloTags;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class JelloBlockVariants {

    private static final Item.Settings itemSettings = new OwoItemSettings()
            .group(ItemGroup.MISC)
            .tab(2)
            .maxCount(64);

    public static final DyeableBlockVariant SLIME_BLOCK = DyeableBlockVariant.Builder.of(Jello.id("slime_block"), itemSettings, (dyeColorant, parentBlock) -> {
        return new SlimeBlockColored(dyeColorant, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
    }).setDefaultEntry(new Identifier("slime_block"))
            .setBlockItemMaker((dyeColorant, block, settings) -> new ColoredBlockItem((Block)block, settings))
            .setBlockTags(JelloTags.Blocks.COLORED_SLIME_BLOCKS, JelloTags.Blocks.SLIME_BLOCKS, JelloTags.Blocks.STICKY_BLOCKS)
            .register();

    public static final DyeableBlockVariant SLIME_SLAB = DyeableBlockVariant.Builder.of(Jello.id("slime_slab"), itemSettings, (dyeColorant, parentBlock) -> {
        return new SlimeSlabColored(dyeColorant, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
    }).setDefaultEntry("slime_slab")
            .setBlockTags(JelloTags.Blocks.COLORED_SLIME_SLABS, JelloTags.Blocks.SLIME_SLABS, JelloTags.Blocks.STICKY_BLOCKS)
            .setLootTable(block -> GelatinLootTables.slabDrops((Block)block).build())
            .register();

//    public static final DyeableBlockVariant GLOW_STONE = DyeableBlockVariant.Builder.of(JelloConstants.id("glowstone"), itemSettings, (dyeColorant, parentBlock) -> {
//        return new ColoredBlock(FabricBlockSettings.copyOf(Blocks.GLOWSTONE).luminance(MathHelper.ceil(15F * ColorUtil.luminance(dyeColorant.getColorComponents()))), dyeColorant);
//    }).setDefaultEntry(new Identifier("glowstone"))
//    .register();

    public static void initialize() {}
}
