package io.wispforest.jello.main.common;

import io.wispforest.jello.main.common.blocks.BlockRegistry;
import io.wispforest.jello.main.common.compat.JelloBlockVariants;
import io.wispforest.jello.main.common.compat.consistencyplus.data.ConsistencyPlusTags;
import io.wispforest.jello.main.common.config.JelloConfig;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.jello.main.common.items.dyebundle.DyeBundlePackets;
import io.wispforest.jello.main.mixin.ducks.InInventoryCraftingPacket;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.util.ModCompatHelpers;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Jello implements ModInitializer{

    public static final Logger DEBUG_LOGGER = LogManager.getLogger(Jello.class);

    public static final String MODID = "jello";

    public static JelloConfig MAIN_CONFIG;

    public static OwoNetChannel CHANNEL = OwoNetChannel.create(new Identifier(MODID, "main"));

    @Override
    public void onInitialize() {
        Jello.initClothConfig();

        //  Block Registry
        FieldRegistrationHandler.register(BlockRegistry.SlimeSlabRegistry.class, MODID, false);

        JelloBlockVariants.init();

        //  StatusEffect Registry
        //FieldRegistrationHandler.register(JelloStatusEffectsRegistry.class, MODID, false);

        //  Item Registry
        FieldRegistrationHandler.register(ItemRegistry.SlimeBallItemRegistry.class, MODID, false);
        FieldRegistrationHandler.register(ItemRegistry.JelloCupItemRegistry.class, MODID, false);
        FieldRegistrationHandler.register(ItemRegistry.MainItemRegistry.class, MODID, false);

        Jello.ColorBlockRegistryCompat();

        Jello.setupOWOPacketStuff();
    }

    //------------------------------------------------------------------------------

    private static void setupOWOPacketStuff(){
        CHANNEL.registerServerbound(InInventoryCraftingPacket.CraftPacket.class, InInventoryCraftingPacket.CraftPacket::craftFromStack);
        CHANNEL.registerServerbound(DyeBundlePackets.ScreenScrollPacket.class, DyeBundlePackets.ScreenScrollPacket::scrollBundle);
    }

    public static void initClothConfig(){
        AutoConfig.register(JelloConfig.class, GsonConfigSerializer::new);

        MAIN_CONFIG = AutoConfig.getConfigHolder(JelloConfig.class).getConfig();
    }

    private static void ColorBlockRegistryCompat(){
//        DefaultColorBlockRegistry.init();

        ModCompatHelpers.getRegistryHelper(Registry.ITEM).runWhenPresent(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, "stone_wall"), item -> ConsistencyPlusColorBlockRegistry.init());
    }

    //------------------------------------------------------------------------------

//    private static class DefaultColorBlockRegistry{
//
//        private static final List<String> BLOCK_TYPE = List.of(
//                "_wool",
//                "_carpet",
//                "_bed",
//                "_terracotta",
//                "_concrete",
//                "_concrete_powder",
//                "_candle",
//                "_candle_cake",
//                "_shulker_box",
//                "_stained_glass",
//                "_stained_glass_pane"
////                "_slime_block",
////                "_slime_slab"
//        );
//
//        private static final List<Block> BLOCK_TYPE_DEFAULT = List.of(
//                Blocks.WHITE_WOOL,
//                Blocks.WHITE_CARPET,
//                Blocks.WHITE_BED,
//                Blocks.TERRACOTTA,
//                Blocks.WHITE_CONCRETE,
//                Blocks.WHITE_CONCRETE_POWDER,
//                Blocks.CANDLE,
//                Blocks.CANDLE_CAKE,
//                Blocks.SHULKER_BOX,
//                Blocks.GLASS,
//                Blocks.GLASS_PANE
////                Blocks.SLIME_BLOCK,
////                BlockRegistry.SlimeSlabRegistry.SLIME_SLAB
//        );
//
//        private static final List<TagKey<Block>> COLORABLE_BLOCK_TAGS = List.of(
//                BlockTags.WOOL,
//                BlockTags.CARPETS,
//                BlockTags.BEDS,
//                BlockTags.TERRACOTTA,
//                JelloTags.Blocks.CONCRETE,
//                JelloTags.Blocks.CONCRETE_POWDER,
//                BlockTags.CANDLES,
//                BlockTags.CANDLE_CAKES,
//                BlockTags.SHULKER_BOXES,
//                JelloTags.Blocks.STAINED_GLASS,
//                JelloTags.Blocks.GLASS_PANES
////                JelloTags.Blocks.SLIME_BLOCKS,
////                JelloTags.Blocks.SLIME_SLABS
//        );
//
//        private static void init(){
//            for(int z = 0; z < BLOCK_TYPE.size(); z++){
//                Map<DyeColorant, Block> blockTypes = new HashMap<>();
//
//                String nameSpace = Registry.BLOCK.getId(BLOCK_TYPE_DEFAULT.get(z)).getNamespace(); //z <= 10 ? "minecraft" : Jello.MODID;
//
//                for(DyeColorant dyeColorant : DyeColorantRegistry.DYE_COLOR){
//                    blockTypes.put(dyeColorant, Registry.BLOCK.get(new Identifier(nameSpace, dyeColorant + BLOCK_TYPE.get(z))));
//                }
//
//                ColorBlockRegistry.registerBlockSetUnsafe(blockTypes, BLOCK_TYPE_DEFAULT.get(z), COLORABLE_BLOCK_TAGS.get(z));
//            }
//        }
//    }

//    private static class DefaultColorBlockRegistry{
//
//        private static final List<String> BLOCK_TYPE = List.of(
//                "_wool",
//                "_carpet",
//                "_bed",
//                "_terracotta",
//                "_concrete",
//                "_concrete_powder",
//                "_candle",
//                "_candle_cake",
//                "_shulker_box",
//                "_stained_glass",
//                "_stained_glass_pane",
//                "_slime_block",
//                "_slime_slab"
//        );
//
//        private static final List<Block> BLOCK_TYPE_DEFAULT = List.of(
//                Blocks.WHITE_WOOL,
//                Blocks.WHITE_CARPET,
//                Blocks.WHITE_BED,
//                Blocks.TERRACOTTA,
//                Blocks.WHITE_CONCRETE,
//                Blocks.WHITE_CONCRETE_POWDER,
//                Blocks.CANDLE,
//                Blocks.CANDLE_CAKE,
//                Blocks.SHULKER_BOX,
//                Blocks.GLASS,
//                Blocks.GLASS_PANE,
//                Blocks.SLIME_BLOCK,
//                BlockRegistry.SlimeSlabRegistry.SLIME_SLAB
//        );
//
//        private static final List<TagKey<Block>> COLORABLE_BLOCK_TAGS = List.of(
//                BlockTags.WOOL,
//                BlockTags.CARPETS,
//                BlockTags.BEDS,
//                BlockTags.TERRACOTTA,
//                JelloTags.Blocks.CONCRETE,
//                JelloTags.Blocks.CONCRETE_POWDER,
//                BlockTags.CANDLES,
//                BlockTags.CANDLE_CAKES,
//                BlockTags.SHULKER_BOXES,
//                JelloTags.Blocks.STAINED_GLASS,
//                JelloTags.Blocks.GLASS_PANES,
//                JelloTags.Blocks.SLIME_BLOCKS,
//                JelloTags.Blocks.SLIME_SLABS
//        );
//
//        private static void init(){
//            for(int z = 0; z < BLOCK_TYPE.size(); z++){
//                List<Block> blockTypes = new ArrayList<>();
//
//                String nameSpace = z <= 10 ? "minecraft" : Jello.MODID;
//
//                for(int i = 0; i < 16; i++){
//                    blockTypes.add(Registry.BLOCK.get(new Identifier(nameSpace, DyeColorantRegistry.Constants.VANILLA_DYES.get(i) + BLOCK_TYPE.get(z))));
//                }
//
//                ColorBlockRegistry.registerBlockSetUnsafe(blockTypes, BLOCK_TYPE_DEFAULT.get(z), COLORABLE_BLOCK_TAGS.get(z));
//            }
//        }
//    }

    //------------------------------------------------------------------------------

    private static class ConsistencyPlusColorBlockRegistry{

        private static final Logger LOGGER = LogManager.getLogger(ConsistencyPlusColorBlockRegistry.class);

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

        private static void fillRegistryFromTags(TagKey<Block> identified){
//            String[] nameParts = identified.id().getPath().split("_");
//
//            String blockNamePrefix;
//            String blockNameSuffix;
//
//            if(BLOCK_PREFIXES.contains(nameParts[0])){
//                blockNamePrefix = nameParts[0];
//                blockNameSuffix = nameParts[1];
//
//                if(nameParts.length == 3){
//                    blockNameSuffix = blockNameSuffix + "_" + BLOCK_TYPE.get(nameParts[nameParts.length - 1]);
//                }
//            }else if(nameParts.length > 1 && BLOCK_SUFFIXES.containsKey(nameParts[1])){
//                blockNamePrefix = null;
//
//                if(nameParts.length == 3 && !(Objects.equals(nameParts[1], "corner"))){
//                    blockNameSuffix = nameParts[0] + "_" + nameParts[1];
//
//                    blockNameSuffix = blockNameSuffix + "_" + BLOCK_TYPE.get(nameParts[nameParts.length - 1]);
//                }else{
//                    blockNameSuffix = nameParts[0] + "_" + BLOCK_SUFFIXES.get(nameParts[1]);
//                }
//            }else {
//                blockNamePrefix = null;
//
//                if (nameParts.length > 1) {
//                    blockNameSuffix = nameParts[0] + "_" + BLOCK_TYPE.get(nameParts[1]);
//                } else {
//                    blockNameSuffix = nameParts[0];
//                }
//            }
//
//            //---------------------------------------------//
//
//            Block defaultBlock;
//
//            if(blockNamePrefix != null){
//                defaultBlock = Registry.BLOCK.get(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNamePrefix + "_" + blockNameSuffix));
//            }else{
//                defaultBlock = Registry.BLOCK.get(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNameSuffix));
//            }
//
//            if(defaultBlock == Blocks.AIR) {
//                boolean minecraftNameSpace = Objects.equals(nameParts[0], "tinted") || (nameParts.length == 1 && (Objects.equals(nameParts[0], "ice") || Objects.equals(nameParts[0], "glowstone")));
//
//                String defaultValue = switch (nameParts[0]) {
//                    case "ice" -> "blue_";
//                    case "tinted", "glowstone" -> "";
//                    default -> "white_";
//                };
//
//                if(!nameParts[0].equals("terracotta")){
//                    if (blockNamePrefix != null) {
//                        defaultBlock = Registry.BLOCK.get(new Identifier(minecraftNameSpace ? "minecraft" : ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNamePrefix + "_" + defaultValue + blockNameSuffix));
//                        LOGGER.info("[1]:" + new Identifier(minecraftNameSpace ? "minecraft" : ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNamePrefix + "_" + defaultValue + blockNameSuffix));
//                    } else {
//                        defaultBlock = Registry.BLOCK.get(new Identifier(minecraftNameSpace ? "minecraft" : ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, defaultValue + blockNameSuffix));
//                        LOGGER.info("[2]:" + new Identifier(minecraftNameSpace ? "minecraft" : ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, defaultValue + blockNameSuffix));
//                    }
//                }else{
//                    if (nameParts.length >= 2) {
//                        if (Objects.equals(nameParts[1], "brick") || Objects.equals(nameParts[1], "bricks")) {
//                            LOGGER.info("FUCKFUCKFUCKF");
//                            defaultBlock = Registry.BLOCK.get(TERRACOTA_BRICK_REMAP.get(nameParts[nameParts.length - 1]));
//                        }
//                    }
//                }
//            }
//
//            //---------------------------------------------//
//
//            List<Block> colorBlockList = new ArrayList<>();
//
//            for(int i = 0; i < 16; i++){
//                if(nameParts[0] == "ice" && DyeColorantRegistry.Constants.VANILLA_DYES.get(i).getName() == "blue") {
//                    colorBlockList.add(Blocks.BLUE_ICE);
//                    defaultBlock = Blocks.BLUE_ICE;
//                    continue;
//                }
//
//                if(blockNamePrefix != null){
//                    colorBlockList.add(Registry.BLOCK.get(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, blockNamePrefix + "_" + DyeColorantRegistry.Constants.VANILLA_DYES.get(i).getName() + "_" + blockNameSuffix)));
//                }else{
//                    colorBlockList.add(Registry.BLOCK.get(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, DyeColorantRegistry.Constants.VANILLA_DYES.get(i).getName() + "_" + blockNameSuffix)));
//                }
//            }
//
//            if(defaultBlock != Blocks.AIR){
//                ColorBlockRegistry.registerBlockSetUnsafe(colorBlockList, defaultBlock, identified);
//            }else{
//                LOGGER.info(nameParts[0] + " / " + colorBlockList + " / " + defaultBlock + " / " + identified.id());
//                //System.out.println(identified.getId() + " / " +  defaultBlock.toString());
//            }
        }
    }





}
