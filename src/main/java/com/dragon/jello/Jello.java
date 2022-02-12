package com.dragon.jello;

import com.dragon.jello.blocks.BlockRegistry;
import com.dragon.jello.events.ColorEntityEvent;
import com.dragon.jello.events.ColorBlockEvent;
import com.dragon.jello.items.ItemRegistry;
//import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class Jello implements ModInitializer, PreLaunchEntrypoint {
    public static final String MODID = "jello";

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(BlockRegistry.SlimeBlockRegistry.class, MODID, false);
        FieldRegistrationHandler.register(BlockRegistry.SlimeSlabRegistry.class, MODID, false);

        FieldRegistrationHandler.register(ItemRegistry.SlimeBallItemRegistry.class, MODID, false);

        registerEvents();

        //ColorizeRegistry.registerColorable(new Identifier("textures/entity/slime/slime.png"), EntityType.SLIME);
//        GrayScaleRegistry.registerGrayScalable(new Identifier(MODID, "textures/entity/slime/slime_grayscale.png"), EntityType.SLIME);
//        GrayScaleRegistry.registerGrayScalable(new Identifier(MODID, "textures/entity/cow/cow_grayscale.png"), EntityType.COW);
    }

    private void registerEvents(){
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->{
            return new ColorEntityEvent().interact(player, world, hand, entity, hitResult);
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            return new ColorBlockEvent().interact(player, world, hand, blockHitResult);
        });
    }

    @Override
    public void onPreLaunch() {
//        MixinExtrasBootstrap.init();
    }
}
