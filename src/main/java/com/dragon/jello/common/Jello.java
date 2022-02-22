package com.dragon.jello.common;

import com.dragon.jello.common.blocks.BlockRegistry;
import com.dragon.jello.common.compat.consistencyplus.data.ConsistencyPlusTags;
import com.dragon.jello.common.data.tags.JelloTags;
import com.dragon.jello.common.effects.JelloStatusEffectsRegistry;
import com.dragon.jello.common.items.ItemRegistry;
import com.dragon.jello.lib.events.ColorBlockEvent;
import com.dragon.jello.lib.events.ColorEntityEvent;
import com.dragon.jello.lib.events.DeColorizeCallback;
import com.dragon.jello.lib.events.behavior.ColorEntityBehavior;
import com.dragon.jello.lib.events.behavior.DeColorEntityBehavior;
import com.dragon.jello.lib.registry.ColorBlockRegistry;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.util.ModCompatHelpers;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//import com.llamalad7.mixinextras.MixinExtrasBootstrap;

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

        ModCompatHelpers.getRegistryHelper(Registry.ITEM).runWhenPresent(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, "stone_wall"), item -> ConsistencyPlusColorBlockRegistry.init());

//        if(FabricLoaderImpl.INSTANCE.isModLoaded("consistency_plus")){
//            ConsistencyPlusColorBlockRegistry.init();
//        }

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
                JelloTags.Blocks.STAINED_GLASS,
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

    private static class ConsistencyPlusColorBlockRegistry{

        private static final Logger LOGGER = LogManager.getLogger(ConsistencyPlusColorBlockRegistry.class);

        private static final DyeColor[] DYE_VALUES = DyeColor.values();

        private static final List<String> BLOCK_PREFIXES = List.of("cobbled", "smooth", "cut", "chiseled", "carved", "polished");

        private static final Map<String, String> BLOCK_SUFFIXES = Map.of(
                "brick", "bricks",
                "tile", "tiles",
                "corner", "corner_pillar",
                "pillar", "pillar",
                "glass", "glass");

        private static final Map<String, String> BLOCK_TYPE = Map.of(
                "gates", "gate",
                "slabs", "slab",
                "stairs", "stairs",
                "walls", "wall");

        private static final Map<String, Identifier> TERRACOTA_BRICK_REMAP = Map.of(
                "brick", new Identifier("bricks"),
                "slabs",  new Identifier("brick_slab"),
                "stairs",  new Identifier("brick_stairs"),
                "walls", new Identifier("brick_wall"),
                "gates", new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID,"brick_gate"));

        private static void init(){
            long startTime = System.currentTimeMillis();

            ConsistencyPlusTags.DyeableBlocks.ALL_DYEABLE_BLOCK_TAGS.forEach(ConsistencyPlusColorBlockRegistry::fillRegistryFromTags);

            long finishTime = System.currentTimeMillis();

            System.out.printf("It took %f seconds for ConsistencyPlus Comp ColorBlock Registry to complete\n", (finishTime - startTime) / 1000.0F);
        }

        private static void fillRegistryFromTags(Tag.Identified<Block> identified){
            String[] nameParts = identified.getId().getPath().split("_");

            String blockNamePrefix;
            String blockNameSuffix;

            if(BLOCK_PREFIXES.contains(nameParts[0])){
                blockNamePrefix = nameParts[0];
                blockNameSuffix = nameParts[1];

                if(nameParts.length == 3){
                    blockNameSuffix = blockNameSuffix + "_" + BLOCK_TYPE.get(nameParts[nameParts.length - 1]);
                }
            }else if(nameParts.length > 1 && BLOCK_SUFFIXES.containsKey(nameParts[1])){
                blockNamePrefix = null;

                if(nameParts.length == 3 && !(Objects.equals(nameParts[1], "corner"))){
                    blockNameSuffix = nameParts[0] + "_" + nameParts[1];

                    blockNameSuffix = blockNameSuffix + "_" + BLOCK_TYPE.get(nameParts[nameParts.length - 1]);
                }else{
                    blockNameSuffix = nameParts[0] + "_" + BLOCK_SUFFIXES.get(nameParts[1]);
                }
            }else {
                blockNamePrefix = null;

                if (nameParts.length > 1) {
                    blockNameSuffix = nameParts[0] + "_" + BLOCK_TYPE.get(nameParts[1]);
                } else {
                    blockNameSuffix = nameParts[0];
                }
            }

            //---------------------------------------------//

            Block defaultBlock;

            if(blockNamePrefix != null){
                defaultBlock = Registry.BLOCK.get(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNamePrefix + "_" + blockNameSuffix));
            }else{
                defaultBlock = Registry.BLOCK.get(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNameSuffix));
            }

            if(defaultBlock == Blocks.AIR) {
                boolean minecraftNameSpace = Objects.equals(nameParts[0], "tinted") || (nameParts.length == 1 && (Objects.equals(nameParts[0], "ice") || Objects.equals(nameParts[0], "glowstone")));

                String defaultValue = switch (nameParts[0]) {
                    case "ice" -> "blue_";
                    case "tinted", "glowstone" -> "";
                    default -> "white_";
                };

                if(!nameParts[0].equals("terracotta")){
                    if (blockNamePrefix != null) {
                        defaultBlock = Registry.BLOCK.get(new Identifier(minecraftNameSpace ? "minecraft" : ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNamePrefix + "_" + defaultValue + blockNameSuffix));
                        LOGGER.info("[1]:" + new Identifier(minecraftNameSpace ? "minecraft" : ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNamePrefix + "_" + defaultValue + blockNameSuffix));
                    } else {
                        defaultBlock = Registry.BLOCK.get(new Identifier(minecraftNameSpace ? "minecraft" : ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, defaultValue + blockNameSuffix));
                        LOGGER.info("[2]:" + new Identifier(minecraftNameSpace ? "minecraft" : ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, defaultValue + blockNameSuffix));
                    }
                }else{
                    if (nameParts.length >= 2) {
                        if (Objects.equals(nameParts[1], "brick") || Objects.equals(nameParts[1], "bricks")) {
                            LOGGER.info("FUCKFUCKFUCKF");
                            defaultBlock = Registry.BLOCK.get(TERRACOTA_BRICK_REMAP.get(nameParts[nameParts.length - 1]));
                        }
                    }
                }
            }

            //---------------------------------------------//

            List<Block> colorBlockList = new ArrayList<>();

            for(int i = 0; i < DYE_VALUES.length; i++){
                if(nameParts[0] == "ice" && DYE_VALUES[i].getName() == "blue") {
                    colorBlockList.add(Blocks.BLUE_ICE);
                    defaultBlock = Blocks.BLUE_ICE;
                    continue;
                }

                if(blockNamePrefix != null){
                    colorBlockList.add(Registry.BLOCK.get(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNamePrefix + "_" + DYE_VALUES[i].getName() + "_" + blockNameSuffix)));
                }else{
                    colorBlockList.add(Registry.BLOCK.get(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, DYE_VALUES[i].getName() + "_" + blockNameSuffix)));
                }
            }

            if(defaultBlock != Blocks.AIR){
                ColorBlockRegistry.registerBlockSetUnsafe(colorBlockList, defaultBlock, identified);
            }else{
                LOGGER.info(nameParts[0] + " / " + colorBlockList + " / " + defaultBlock + " / " + identified.getId());
                //System.out.println(identified.getId() + " / " +  defaultBlock.toString());
            }
        }
    }


}
