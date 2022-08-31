package io.wispforest.cauldron;

import io.wispforest.common.CommonInit;
import io.wispforest.common.compat.GelatinConfig;
import io.wispforest.common.misc.GelatinConstants;
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
import net.minecraft.util.ActionResult;

public class CauldronClientInit implements ClientModInitializer {

    private static final RenderLayer TRANSLUCENT = RenderLayer.getTranslucent();

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer("cauldron").ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(GelatinConstants.id("cauldron_cull_fix"), container, ResourcePackActivationType.DEFAULT_ENABLED);
        });

        CommonInit.MAIN_CONFIG.registerSaveListener((configHolder, jelloConfig) -> {
            toggleRenderLayer(jelloConfig);

            return ActionResult.PASS;
        });

        CommonInit.MAIN_CONFIG.registerLoadListener((configHolder, jelloConfig) -> {
            toggleRenderLayer(jelloConfig);

            return ActionResult.PASS;
        });

        ColorProviderRegistry.BLOCK.register((BlockColorProvider) Blocks.WATER_CAULDRON, Blocks.WATER_CAULDRON);

        toggleRenderLayer(CommonInit.getConfig());
    }

    private static void toggleRenderLayer(GelatinConfig jelloConfig){
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.WATER_CAULDRON, jelloConfig.enableTransparencyFixCauldrons ? TRANSLUCENT : RenderLayer.getSolid());

        if(MinecraftClient.getInstance().worldRenderer != null){
            MinecraftClient.getInstance().worldRenderer.reload();
        }


    }

}
