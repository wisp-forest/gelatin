package io.wispforest.cauldron;

import io.wispforest.cauldron.blockentity.GelatinBlockEntityTypes;
import io.wispforest.common.misc.GelatinConstants;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class CauldronInit implements ModInitializer {

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(GelatinBlockEntityTypes.class, GelatinConstants.MODID, false);

        GelatinCauldronBehaviors.registerJelloBehaviorBypass();
    }
}
