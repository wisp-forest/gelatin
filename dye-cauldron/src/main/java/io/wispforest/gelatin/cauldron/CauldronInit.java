package io.wispforest.gelatin.cauldron;

import io.wispforest.gelatin.cauldron.blockentity.GelatinBlockEntityTypes;
import net.fabricmc.api.ModInitializer;

public class CauldronInit implements ModInitializer {

    @Override
    public void onInitialize() {
        GelatinBlockEntityTypes.init();

        GelatinCauldronBehaviors.registerJelloBehaviorBypass();
    }
}
