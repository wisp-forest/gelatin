package io.wispforest.gelatin.dye_entities.client;

import io.wispforest.gelatin.common.CommonInit;
import io.wispforest.gelatin.dye_entities.client.utils.ColorizeBlackListRegistry;
import io.wispforest.gelatin.dye_entities.client.utils.GrayScaleEntityRegistry;
import net.fabricmc.api.ClientModInitializer;

public class DyeEntityApiClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CommonInit.getConfig().addGelatinBlacklistSubscriber(blacklistedModids -> {
            GrayScaleEntityRegistry.GRAYSCALABLE_MODID_BLACKLIST.addAll(blacklistedModids);
            ColorizeBlackListRegistry.MODID_BLACKLIST.addAll(blacklistedModids);
        });

        GrayScaleEntityRegistry.GRAYSCALABLE_MODID_BLACKLIST.addAll(CommonInit.getConfig().getGelatinBlacklist());
        ColorizeBlackListRegistry.MODID_BLACKLIST.addAll(CommonInit.getConfig().getGelatinBlacklist());
    }
}
