package io.wispforest.jello.api;

import io.wispforest.jello.api.dye.events.ColorEntityEvent;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.util.TrackedDataHandlerExtended;
import io.wispforest.jello.misc.behavior.ColorEntityBehavior;
import io.wispforest.jello.misc.behavior.JelloCauldronBehaviors;
import io.wispforest.jello.misc.behavior.WashEntityBehavior;
import io.wispforest.jello.misc.dye.DyeColorantLoader;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.registration.reflect.SimpleFieldProcessingSubject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

import java.lang.reflect.Field;

public class JelloAPI implements ModInitializer {

    public static final String MODID = "jello_api";

    @Override
    public void onInitialize() {
        //  Ext. TrackedData Registry
        FieldRegistrationHandler.processSimple(TrackedDataHandlerExtended.class, false);

//        VanillaBlockBuilder.init();

        DyeColorantRegistry.initVanillaDyes();

        // just leave it here - patpat
        DyeColorantLoader.loadFromJson();
//        DyeColorantRegistry.initVanillaDyes();

        // pogchamp
        ((OwoItemGroup) ItemGroup.MISC).initialize();

        JelloCauldronBehaviors.registerJelloBehaviorBypass();

        //  GameEvent Registry
        FieldRegistrationHandler.register(GameEvents.class, MODID, false);

        //  Stats Registry
        FieldRegistrationHandler.processSimple(Stats.class, false);

        JelloAPI.registerDispenserBehavior();

        JelloAPI.registerEvents();
    }

    //------------------------------------------------------------------------------

    private static void registerDispenserBehavior() {
        final var behavior = new ColorEntityBehavior();

        for (var colorant : DyeColorantRegistry.DYE_COLOR) {
            final var id = colorant.getId();
            DispenserBlock.registerBehavior(Registry.ITEM.get(new Identifier(id.getNamespace(), id.getPath() + "_dye")), behavior);
        }

        DispenserBlock.registerBehavior(Items.WATER_BUCKET, new WashEntityBehavior());
    }

    private static void registerEvents() {
        UseEntityCallback.EVENT.register(new ColorEntityEvent());
    }

    //------------------------------------------------------------------------------

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

    //------------------------------------------------------------------------------

    public static class Stats implements SimpleFieldProcessingSubject<Identifier> {

        public static final Identifier CLEAN_BLOCK = new Identifier(MODID, "clean_block");

        public static final Identifier DYE_BLOCK = new Identifier(MODID, "dye_block");
        public static final Identifier DYE_ARMOR = new Identifier(MODID, "dye_armor");
        //public static final Identifier DYE_BANNER = new Identifier(MODID, "dye_banner");
        public static final Identifier DYE_SHULKER_BOX = new Identifier(MODID, "dye_shulker_box");

        @Override
        public void processField(Identifier value, String identifier, Field field) {
            Registry.register(Registry.CUSTOM_STAT, identifier, value);
            net.minecraft.stat.Stats.CUSTOM.getOrCreateStat(value, StatFormatter.DEFAULT);
        }

        @Override
        public Class<Identifier> getTargetFieldType() {
            return Identifier.class;
        }
    }

    //------------------------------------------------------------------------------
}
