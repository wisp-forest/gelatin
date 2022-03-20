package io.wispforest.jello.api;

import io.wispforest.jello.api.dye.DyeColorantJsonTest;
import io.wispforest.jello.api.dye.client.BlockModelRedirect;
import io.wispforest.jello.api.dye.item.DyeItem;
import io.wispforest.jello.api.dye.client.DyeModelResourceRedirect;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class JelloClientAPI implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.BLOCK.register((BlockColorProvider) Blocks.WATER_CAULDRON, Blocks.WATER_CAULDRON);
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.WATER_CAULDRON, RenderLayer.getTranslucent());

        registerCustomModelPredicate();

        initJsonDyeItems();

        registerJsonBlocksForColor();
    }

    //------------------------------------------------------------------------------

    private static void initJsonDyeItems(){
        DyeColorantJsonTest.JSON_DYES.forEach(dyeItem -> {
            ColorProviderRegistry.ITEM.register(dyeItem, dyeItem);
        });

        ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> {
            return new DyeModelResourceRedirect();
        });

        ModelLoadingRegistry.INSTANCE.registerVariantProvider(resourceManager -> {
            return new BlockModelRedirect();
        });
    }

    private static void registerCustomModelPredicate(){
        DyeColorantJsonTest.JSON_DYES.forEach(dyeItem -> {
            FabricModelPredicateProviderRegistry.register(dyeItem, new Identifier("variant"), (stack, world, entity, seed) -> DyeItem.getTextureVariant(stack));
        });
    }

    private static void registerJsonBlocksForColor(){
        List<List<Block>> blockVarList = DyeColorantJsonTest.JSON_BLOCK_VAR;

        for(List<Block> blockList : blockVarList){
            for(Block block : blockList){
                if(!(block instanceof ShulkerBoxBlock)) {
                    ColorProviderRegistry.BLOCK.register((BlockColorProvider) block, block);
                    ColorProviderRegistry.ITEM.register((ItemColorProvider) block.asItem(), block.asItem());
                }else{
                    BuiltinItemRendererRegistry.INSTANCE.register(block, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
//                        System.out.println("test1");

                        ShulkerBoxBlockEntity shulkerBoxBlockEntity = new ShulkerBoxBlockEntity(DyeColorRegistry.NULL_VALUE_OLD, BlockPos.ORIGIN, block.getDefaultState());

                        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(shulkerBoxBlockEntity).render(shulkerBoxBlockEntity, 0.0F, matrices, vertexConsumers, light, overlay);

//                        System.out.println("test2");
                    });
                }
            }
        }
    }

    //------------------------------------------------------------------------------

}
