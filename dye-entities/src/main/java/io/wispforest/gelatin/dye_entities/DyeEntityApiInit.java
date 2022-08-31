package io.wispforest.gelatin.dye_entities;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entities.behaviors.ColorEntityBehavior;
import io.wispforest.gelatin.dye_entities.behaviors.WashEntityBehavior;
import io.wispforest.gelatin.dye_entities.misc.GelatinGameEvents;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DyeEntityApiInit implements ModInitializer {

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(GelatinGameEvents.class, GelatinConstants.MODID, false);

        registerDispenserBehavior();
    }

    //TODO: Change this to a event based system
    public static void registerDispenserBehavior() {
        final var behavior = new ColorEntityBehavior();

        for (var colorant : DyeColorantRegistry.DYE_COLOR) {
            final var id = colorant.getId();
            DispenserBlock.registerBehavior(Registry.ITEM.get(new Identifier(id.getNamespace(), id.getPath() + "_dye")), behavior);
        }

        DispenserBlock.registerBehavior(Items.WATER_BUCKET, new WashEntityBehavior());
    }
}
