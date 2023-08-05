package io.wispforest.gelatin.cauldron;

import io.wispforest.gelatin.common.CommonInit;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.RenderLayer;

public class CauldronClientInit implements ClientModInitializer {

    private static final RenderLayer TRANSLUCENT = RenderLayer.getTranslucent();

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer("cauldron").ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(GelatinConstants.id("cauldron_cull_fix"), container, ResourcePackActivationType.DEFAULT_ENABLED);
        });

        CommonInit.getConfig().observeCauldronFix(CauldronClientInit::toggleRenderLayer);

        ColorProviderRegistry.BLOCK.register((BlockColorProvider) Blocks.WATER_CAULDRON, Blocks.WATER_CAULDRON);

        toggleRenderLayer(CommonInit.getConfig().cauldronFix());
    }

    private static void toggleRenderLayer(boolean enableTransparencyFixCauldrons){
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.WATER_CAULDRON, enableTransparencyFixCauldrons ? TRANSLUCENT : RenderLayer.getSolid());

        if(MinecraftClient.getInstance().worldRenderer != null){
            MinecraftClient.getInstance().worldRenderer.reload();
        }
    }

}
