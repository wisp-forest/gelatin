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
import com.dragon.jello.lib.registry.ColorBlockRegistry;
import com.dragon.jello.mixin.ducks.BounceEffectMethod;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.List;

public class Jello implements ModInitializer, PreLaunchEntrypoint {
    public static final String MODID = "jello";

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(BlockRegistry.SlimeBlockRegistry.class, MODID, false);
        FieldRegistrationHandler.register(BlockRegistry.SlimeSlabRegistry.class, MODID, false);

        DefaultColorBlockRegistry.init();

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

//        LivingEntityTickEvents.START_TICK.register(BounceEffectMethod::bounce);

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

    private static class DefaultColorBlockRegistry{
        private static final DyeColor[] DYE_VALUES = DyeColor.values();

        private static final List<String> BLOCK_TYPE = List.of(
                "_wool",
                "_carpet",
                "_bed",
                "_terracotta",
                "_concrete",
                "_concrete_powder",
                "_candle",
                "_candle_cake",
                "_shulker_box",
                "_stained_glass",
                "_stained_glass_pane",
                "_slime_block",
                "_slime_slab"
        );

        private static final List<Block> BLOCK_TYPE_DEFAULT = List.of(
                Blocks.WHITE_WOOL,
                Blocks.WHITE_CARPET,
                Blocks.WHITE_BED,
                Blocks.TERRACOTTA,
                Blocks.WHITE_CONCRETE,
                Blocks.WHITE_CONCRETE_POWDER,
                Blocks.CANDLE,
                Blocks.CANDLE_CAKE,
                Blocks.SHULKER_BOX,
                Blocks.GLASS,
                Blocks.GLASS_PANE,
                Blocks.SLIME_BLOCK,
                BlockRegistry.SlimeSlabRegistry.SLIME_SLAB
        );

        private static final List<Tag.Identified<Block>> COLORABLE_BLOCK_TAGS = List.of(
                BlockTags.WOOL,
                BlockTags.CARPETS,
                BlockTags.BEDS,
                BlockTags.TERRACOTTA,
                JelloTags.Blocks.CONCRETE,
                JelloTags.Blocks.CONCRETE_POWDER,
                BlockTags.CANDLES,
                BlockTags.CANDLE_CAKES,
                BlockTags.SHULKER_BOXES,
                BlockTags.IMPERMEABLE,
                JelloTags.Blocks.GLASS_PANES,
                JelloTags.Blocks.SLIME_BLOCKS,
                JelloTags.Blocks.SLIME_SLABS
        );

        private static void init(){
            for(int z = 0; z < BLOCK_TYPE.size(); z++){
                List<Block> blockTypes = new ArrayList<>();

                String nameSpace = z <= 10 ? "minecraft" : Jello.MODID;

                for(int i = 0; i < DYE_VALUES.length; i++){
                    blockTypes.add(Registry.BLOCK.get(new Identifier(nameSpace, DYE_VALUES[i] + BLOCK_TYPE.get(z))));
                }

                ColorBlockRegistry.registerBlockSetUnsafe(blockTypes, BLOCK_TYPE_DEFAULT.get(z), COLORABLE_BLOCK_TAGS.get(z));
            }
        }
    }


}
