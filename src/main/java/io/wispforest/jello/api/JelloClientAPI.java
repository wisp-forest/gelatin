package io.wispforest.jello.api;

import io.wispforest.jello.api.dye.RandomDyeColorStuff;
import io.wispforest.jello.api.dye.item.DyeItem;
import io.wispforest.jello.api.dye.item.group.DyeModelResourceRedirect;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.jello.main.common.items.SpongeItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BundleItem;
import net.minecraft.util.Identifier;

public class JelloClientAPI implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ColorProviderRegistry.BLOCK.register((BlockColorProvider) Blocks.WATER_CAULDRON, Blocks.WATER_CAULDRON);
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.WATER_CAULDRON, RenderLayer.getTranslucent());

        registerCustomModelPredicate();

        initJsonDyeItems();

    }

    //------------------------------------------------------------------------------

    private static void initJsonDyeItems(){
        RandomDyeColorStuff.JSON_DYES.forEach(dyeItem -> {
            ColorProviderRegistry.ITEM.register(dyeItem, dyeItem);
        });

        ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> {
            return new DyeModelResourceRedirect();
        });
    }

    private static void registerCustomModelPredicate(){
        RandomDyeColorStuff.JSON_DYES.forEach(dyeItem -> {
            FabricModelPredicateProviderRegistry.register(dyeItem, new Identifier("variant"), (stack, world, entity, seed) -> DyeItem.getTextureVariant(stack));
        });
    }

    //------------------------------------------------------------------------------

}
