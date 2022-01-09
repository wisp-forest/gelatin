package com.dragon.jello.tags;

import com.dragon.jello.Jello;
import net.fabricmc.fabric.api.tag.FabricTagBuilder;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.data.server.BlockTagsProvider;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class JelloBlockTags {
    public static final Tag.Identified<Block> CONCRETE = TagFactory.BLOCK.create(new Identifier(Jello.MODID, "concrete"));

    public static final Tag.Identified<Block> COLORED_GLASS_PANES = TagFactory.BLOCK.create(new Identifier(Jello.MODID, "colored_panes"));

}
