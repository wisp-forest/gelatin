package io.wispforest.gelatin.cauldron;

import io.wispforest.gelatin.cauldron.blockentity.GelatinBlockEntityTypes;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;

public class CauldronInit implements ModInitializer {

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(GelatinBlockEntityTypes.class, GelatinConstants.MODID, false);

        GelatinCauldronBehaviors.registerJelloBehaviorBypass();
    }
}
