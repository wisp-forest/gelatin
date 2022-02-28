package com.dragon.jello.client;

import com.dragon.jello.common.blocks.BlockRegistry;
import com.dragon.jello.common.items.ItemRegistry;
import com.dragon.jello.common.items.SpongeItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class JelloClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block)->{
            ColorProviderRegistry.BLOCK.register((BlockColorProvider)block, block);
            ColorProviderRegistry.ITEM.register((ItemColorProvider)block.asItem(), block.asItem());

            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
        });

        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.SlimeSlabRegistry.SLIME_SLAB, RenderLayer.getTranslucent());

        BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block)->{
            ColorProviderRegistry.BLOCK.register((BlockColorProvider)block, block);
            ColorProviderRegistry.ITEM.register((ItemColorProvider)block.asItem(), block.asItem());

            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
        });

        ColorProviderRegistry.BLOCK.register((BlockColorProvider)Blocks.WATER_CAULDRON, Blocks.WATER_CAULDRON);
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.WATER_CAULDRON, RenderLayer.getTranslucent());

       // BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.MainBlockRegistry.WATER_CAULDRON, RenderLayer.getTranslucent());


        ItemRegistry.SlimeBallItemRegistry.SLIME_BALLS.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider)item, item));

        ItemRegistry.JelloCupItemRegistry.JELLO_CUP.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider)item, item));

        FabricModelPredicateProviderRegistry.register(ItemRegistry.MainItemRegistry.SPONGE, new Identifier("dirtiness"), (stack, world, entity, seed) -> SpongeItem.getDirtinessStage(stack));
    }
}
