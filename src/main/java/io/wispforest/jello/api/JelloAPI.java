package io.wispforest.jello.api;

import io.wispforest.jello.api.dye.behavior.ColorEntityBehavior;
import io.wispforest.jello.api.dye.behavior.DeColorEntityBehavior;
import io.wispforest.jello.api.dye.blockentity.BlockEntityRegistry;
import io.wispforest.jello.api.dye.events.ColorEntityEvent;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.dye.RandomDyeColorStuff;
import io.wispforest.jello.api.dye.behavior.cauldron.JelloCauldronBehaviors;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.api.util.TrackedDataHandlerExtended;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.common.blocks.BlockRegistry;
import io.wispforest.jello.main.common.compat.consistencyplus.data.ConsistencyPlusTags;
import io.wispforest.jello.main.common.data.tags.JelloTags;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.registration.reflect.SimpleFieldProcessingSubject;
import io.wispforest.owo.util.ModCompatHelpers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.stat.StatFormatter;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JelloAPI implements ModInitializer {

    public static final String MODID = "jello_api";

    @Override
    public void onInitialize() {

        FieldRegistrationHandler.register(BlockEntityRegistry.class, MODID,false);

        //  Ext. TrackedData Registry
        FieldRegistrationHandler.processSimple(TrackedDataHandlerExtended.class, false);

        //DyeColorRegistry.generateJsonFile();
        //System.exit(0);

        RandomDyeColorStuff.gatherDyesFromJson();

        ((OwoItemGroup)ItemGroup.MISC).initialize();

        JelloCauldronBehaviors.registerJelloBehaviorBypass();

        //  GameEvent Registry
        FieldRegistrationHandler.register(GameEvents.class, MODID, false);

        //  Stats Registry
        FieldRegistrationHandler.processSimple(Stats.class, false);

        JelloAPI.registerDispenserBehavior();

        JelloAPI.registerEvents();
    }

    //------------------------------------------------------------------------------

    private static void registerDispenserBehavior(){
        DyeColor[] dyeColors = DyeColor.values();

        for(int i = 0; i < dyeColors.length; i++){
            Item item = Registry.ITEM.get(new Identifier(dyeColors[i].getName() + "_dye"));

            DispenserBlock.registerBehavior(item, new ColorEntityBehavior());
        }

        DispenserBlock.registerBehavior(Items.WATER_BUCKET, new DeColorEntityBehavior());
    }

    private static void registerEvents(){
        UseEntityCallback.EVENT.register(
                (player, world, hand, entity, hitResult) -> new ColorEntityEvent().interact(player, world, hand, entity, hitResult));

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
