package com.dragon.jello.common;

import com.dragon.jello.common.blocks.BlockRegistry;
import com.dragon.jello.common.data.tags.JelloTags;
import com.dragon.jello.lib.events.ColorBlockEvent;
import com.dragon.jello.lib.events.ColorEntityEvent;
import com.dragon.jello.lib.events.DeColorizeCallback;
import com.dragon.jello.common.effects.JelloStatusEffectsRegistry;
import com.dragon.jello.lib.events.LivingEntityTickEvents;
import com.dragon.jello.lib.events.behavior.ColorEntityBehavior;
import com.dragon.jello.lib.events.behavior.DeColorEntityBehavior;
import com.dragon.jello.common.items.ItemRegistry;
//import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class Jello implements ModInitializer, PreLaunchEntrypoint {
    public static final String MODID = "jello";

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(BlockRegistry.SlimeBlockRegistry.class, MODID, false);
        FieldRegistrationHandler.register(BlockRegistry.SlimeSlabRegistry.class, MODID, false);

        FieldRegistrationHandler.register(JelloStatusEffectsRegistry.class, MODID, false);

        FieldRegistrationHandler.register(ItemRegistry.SlimeBallItemRegistry.class, MODID, false);
        FieldRegistrationHandler.register(ItemRegistry.JelloCupItemRegistry.class, MODID, false);
        FieldRegistrationHandler.register(GameEvents.class, MODID, false);

        registerDyeDispenserBehavior();
        DispenserBlock.registerBehavior(Items.WATER_BUCKET, new DeColorEntityBehavior());

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

        DeColorizeCallback.EVENT.register((itemStack, world, user) -> {
            return new ColorEntityEvent().finishUsing(itemStack, world, user);
        });
    }

    private void registerDyeDispenserBehavior(){
        DyeColor[] dyeColors = DyeColor.values();

        for(int i = 0; i < dyeColors.length; i++){
            Item item = Registry.ITEM.get(new Identifier(dyeColors[i].getName() + "_dye"));

            DispenserBlock.registerBehavior(item, new ColorEntityBehavior());
        }
    }

    @Override
    public void onPreLaunch() {
//        MixinExtrasBootstrap.init();
    }

    public static class GameEvents implements AutoRegistryContainer<GameEvent> {

        public static final GameEvent DYE_ENTITY = new GameEvent("dye_entity", 16);

        @Override
        public Registry<GameEvent> getRegistry() {
            return Registry.GAME_EVENT;
        }

        @Override
        public Class<GameEvent> getTargetFieldType() {
            return GameEvent.class;
        }
    }
}
