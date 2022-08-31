package io.wispforest.dye_entities.client;

import io.wispforest.common.CommonInit;
import io.wispforest.dye_entities.client.utils.ColorizeBlackListRegistry;
import io.wispforest.dye_entities.client.utils.GrayScaleEntityRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.ActionResult;

public class DyeEntityApiClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CommonInit.MAIN_CONFIG.registerSaveListener((configHolder, jelloConfig) -> {
            GrayScaleEntityRegistry.GRAYSCALABLE_MODID_BLACKLIST.addAll(jelloConfig.grayScaledBlackListModid);
            ColorizeBlackListRegistry.MODID_BLACKLIST.addAll(jelloConfig.grayScaledBlackListModid);

            return ActionResult.PASS;
        });

        CommonInit.MAIN_CONFIG.registerLoadListener((configHolder, jelloConfig) -> {
            GrayScaleEntityRegistry.GRAYSCALABLE_MODID_BLACKLIST.addAll(jelloConfig.grayScaledBlackListModid);
            ColorizeBlackListRegistry.MODID_BLACKLIST.addAll(jelloConfig.grayScaledBlackListModid);

            return ActionResult.PASS;
        });

    }
}
