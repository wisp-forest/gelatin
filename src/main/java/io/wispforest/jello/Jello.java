package io.wispforest.jello;

import io.wispforest.jello.api.item.RecipeSpecificRemainders;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.blockentity.JelloBlockEntityTypes;
import io.wispforest.jello.client.render.screen.ColorMixerScreenHandler;
import io.wispforest.jello.client.render.screen.JelloScreenHandlerTypes;
import io.wispforest.jello.misc.dye.JelloBlockVariants;
import io.wispforest.jello.compat.consistencyplus.data.ConsistencyPlusTags;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.dyebundle.DyeBundlePackets;
import io.wispforest.jello.compat.JelloConfig;
import io.wispforest.jello.network.ColorMixerScrollPacket;
import io.wispforest.jello.network.ColorMixerSearchPacket;
import io.wispforest.jello.data.recipe.JelloRecipeSerializers;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.util.ModCompatHelpers;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class Jello implements ModInitializer {

    public static final String MODID = "jello";

    private static JelloConfig MAIN_CONFIG;
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(new Identifier(MODID, "main"));

    @Override
    public void onInitialize() {
        RecipeSpecificRemainders.add(Jello.id("sponge_item_from_wet_sponge"), Items.SHEARS);
        RecipeSpecificRemainders.add(Jello.id("sponge_item_from_dry_sponge"), Items.SHEARS);
        RecipeSpecificRemainders.add(Jello.id("artist_palette"), Items.SHEARS);

        JelloScreenHandlerTypes.initialize();

        AutoConfig.register(JelloConfig.class, GsonConfigSerializer::new);
        MAIN_CONFIG = AutoConfig.getConfigHolder(JelloConfig.class).getConfig();

        // Block Registry
        FieldRegistrationHandler.register(JelloBlocks.class, MODID, false);
        FieldRegistrationHandler.register(JelloBlockEntityTypes.class, MODID, false);

        JelloBlockVariants.initialize();

        // StatusEffect Registry
        //FieldRegistrationHandler.register(JelloStatusEffectsRegistry.class, MODID, false);

        //  Item Registry
        FieldRegistrationHandler.register(JelloItems.class, MODID, true);
        FieldRegistrationHandler.register(JelloRecipeSerializers.class, MODID, false);

        Jello.ColorBlockRegistryCompat();

        Jello.initializeNetworking();
    }

    public static JelloConfig getConfig() {
        return MAIN_CONFIG;
    }

    //------------------------------------------------------------------------------

    private static void initializeNetworking() {
        CHANNEL.registerServerbound(DyeBundlePackets.ScreenScrollPacket.class, DyeBundlePackets.ScreenScrollPacket::scrollBundle);

        CHANNEL.registerServerbound(ColorMixerSearchPacket.class, (message, access) -> {
            if (!(access.player().currentScreenHandler instanceof ColorMixerScreenHandler handler)) return;
            handler.search(message.searchFieldContent());
        });

        CHANNEL.registerServerbound(ColorMixerScrollPacket.class, (message, access) -> {
            if (!(access.player().currentScreenHandler instanceof ColorMixerScreenHandler handler)) return;
            handler.scrollItems(message.progress());
        });
    }

    private static void ColorBlockRegistryCompat() {
        ModCompatHelpers.getRegistryHelper(Registry.ITEM).runWhenPresent(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, "stone_wall"), item -> ConsistencyPlusColorBlockRegistry.init());
    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    //------------------------------------------------------------------------------

    private static class ConsistencyPlusColorBlockRegistry {

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
                "slabs", new Identifier("brick_slab"),
                "stairs", new Identifier("brick_stairs"),
                "walls", new Identifier("brick_wall"),
                "gates", new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, "brick_gate"));

        private static void init() {
            long startTime = System.currentTimeMillis();

            ConsistencyPlusTags.DyeableBlocks.ALL_DYEABLE_BLOCK_TAGS.forEach(ConsistencyPlusColorBlockRegistry::fillRegistryFromTags);

            long finishTime = System.currentTimeMillis();

            System.out.printf("It took %f seconds for ConsistencyPlus Comp ColorBlock Registry to complete\n", (finishTime - startTime) / 1000.0F);
        }

        private static void fillRegistryFromTags(TagKey<Block> identified) {
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
