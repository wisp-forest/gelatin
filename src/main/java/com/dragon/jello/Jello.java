package com.dragon.jello;

import com.dragon.jello.data.JelloTagsProvider;
import com.dragon.jello.events.ColorEntityEvent;
import com.dragon.jello.events.ColorBlockEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class Jello implements ClientModInitializer, ModInitializer, DataGeneratorEntrypoint {
    public static final String MODID = "jello";

    @Override
    public void onInitialize() {
        registerEvents();
    }

    @Override
    public void onInitializeClient() {}

    private void registerEvents(){
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->{
            return new ColorEntityEvent().interact(player, world, hand, entity, hitResult);
        });
        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            return new ColorBlockEvent().interact(player, world, hand, blockHitResult);
        });
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(new JelloTagsProvider.BlockTagProvider(fabricDataGenerator));
    }
}
