package io.wispforest.jello;

import io.wispforest.jello.api.ducks.DyeBlockStorage;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.api.item.RecipeSpecificRemainders;
import io.wispforest.jello.api.util.TrackedDataHandlerExtended;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.blockentity.JelloBlockEntityTypes;
import io.wispforest.jello.client.render.screen.ColorMixerScreenHandler;
import io.wispforest.jello.client.render.screen.JelloScreenHandlerTypes;
import io.wispforest.jello.data.loot.JelloLootTables;
import io.wispforest.jello.misc.dye.JelloGameEvents;
import io.wispforest.jello.misc.JelloItemGroup;
import io.wispforest.jello.misc.dye.JelloStats;
import io.wispforest.jello.misc.behavior.ColorEntityBehavior;
import io.wispforest.jello.misc.behavior.JelloCauldronBehaviors;
import io.wispforest.jello.misc.behavior.WashEntityBehavior;
import io.wispforest.jello.misc.dye.DyeColorantLoader;
import io.wispforest.jello.misc.dye.JelloBlockVariants;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.dyebundle.DyeBundlePackets;
import io.wispforest.jello.compat.JelloConfig;
import io.wispforest.jello.network.ColorMixerScrollPacket;
import io.wispforest.jello.network.ColorMixerSearchPacket;
import io.wispforest.jello.data.recipe.JelloRecipeSerializers;
import io.wispforest.jello.network.CustomJsonColorSync;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Jello implements ModInitializer {

    public static final String MODID = "jello";

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(new Identifier(MODID, "main"));

    public static final OwoItemGroup MAIN_ITEM_GROUP = new JelloItemGroup(id("jello_group"));

    public static final boolean DEBUG_ENV_VAR = Boolean.getBoolean("jello.debug");
    public static final boolean DEBUG_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();

    private static ConfigHolder<JelloConfig> MAIN_CONFIG;

    @Override
    public void onInitialize() {

        AutoConfig.register(JelloConfig.class, GsonConfigSerializer::new);

        MAIN_CONFIG = AutoConfig.getConfigHolder(JelloConfig.class);

        DyeColorantRegistry.registerModidModelRedirect(Jello.MODID);

        //----------------------------[Independent Api Stuff's]----------------------------
        DyeColorantRegistry.initVanillaDyes();

        setDyeColorantForMinecraftBlocks();

        JelloCauldronBehaviors.registerJelloBehaviorBypass();
        //---------------------------------------------------------------------------------


        //-------------------------------[Entry Registry's]--------------------------------
        FieldRegistrationHandler.register(JelloBlocks.class, MODID, false);

        FieldRegistrationHandler.register(JelloBlockEntityTypes.class, MODID, false);

        FieldRegistrationHandler.register(JelloItems.class, MODID, true);

        FieldRegistrationHandler.register(JelloRecipeSerializers.class, MODID, false);

        FieldRegistrationHandler.register(JelloGameEvents.class, MODID, false);

        FieldRegistrationHandler.register(JelloScreenHandlerTypes.class, MODID, false);

        FieldRegistrationHandler.processSimple(TrackedDataHandlerExtended.class, false);

        FieldRegistrationHandler.processSimple(JelloStats.class, false);

        JelloBlockVariants.initialize();
        //---------------------------------------------------------------------------------


        //---------------------------------------------------------------------------------

        RecipeSpecificRemainders.add(Jello.id("sponge_item_from_wet_sponge"), Items.SHEARS);
        RecipeSpecificRemainders.add(Jello.id("sponge_item_from_dry_sponge"), Items.SHEARS);
        RecipeSpecificRemainders.add(Jello.id("artist_palette"), Items.SHEARS);

        JelloLootTables.registerLootTablesGeneration();

        if(getConfig().addCustomJsonColors) {
            DyeColorantLoader.loadFromJson();
        }

        registerDispenserBehavior();

        initializeNetworking();
    }


    public static JelloConfig getConfig() {
        return MAIN_CONFIG.getConfig();
    }

    //------------------------------------------------------------------------------

    private static void initializeNetworking() {
        CHANNEL.registerServerbound(DyeBundlePackets.ScreenScrollPacket.class, DyeBundlePackets.ScreenScrollPacket::scrollBundle);

        //------------------------------------------------------------------

        CHANNEL.registerServerbound(ColorMixerSearchPacket.class, (message, access) -> {
            if (!(access.player().currentScreenHandler instanceof ColorMixerScreenHandler handler)) return;
            handler.search(message.searchFieldContent());
        });

        CHANNEL.registerServerbound(ColorMixerScrollPacket.class, (message, access) -> {
            if (!(access.player().currentScreenHandler instanceof ColorMixerScreenHandler handler)) return;
            handler.scrollItems(message.progress());
        });

        //------------------------------------------------------------------

        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            sender.sendPacket(id("json_color_sync"), PacketByteBufs.create());
        });

        ServerLoginNetworking.registerGlobalReceiver(id("json_color_sync"), (server, handler, understood, buf, synchronizer, responseSender) -> {
            CustomJsonColorSync.confirmMatchingConfigOptions(buf.getBoolean(0), handler);
        });

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

    //TODO: GET WORKING AGAIN WITHIN THE FUTURE
//    private static void ColorBlockRegistryCompat() {
//        ModCompatHelpers.getRegistryHelper(Registry.ITEM).runWhenPresent(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, "stone_wall"), item -> ConsistencyPlusColorBlockRegistry.init());
//    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    //------------------------------------------------------------------------------

    public void setDyeColorantForMinecraftBlocks(){
        List<String> allVanillaBlockVariants = VanillaBlockVariants.ALL_VANILLA_VARIANTS.stream().map(dyeableBlockVariant -> dyeableBlockVariant.variantIdentifier.getPath()).collect(Collectors.toList());

        Set<Map.Entry<RegistryKey<Block>, Block>> coloredBlocks = Registry.BLOCK.getEntrySet().stream().filter(entry -> {
            Identifier entryId = entry.getKey().getValue();

            if(Objects.equals(entryId.getNamespace(), "minecraft")){
                for(String vanillaBlockVariant : allVanillaBlockVariants){
                    if(entryId.getPath().contains(vanillaBlockVariant)){
                        return true;
                    }
                }
            }

            return false;
        }).collect(Collectors.toSet());


        for(Map.Entry<RegistryKey<Block>, Block> entry : coloredBlocks){
            String entryPath = entry.getKey().getValue().getPath();
            Block block = entry.getValue();

            for(DyeColorant vanillaColor : DyeColorantRegistry.Constants.VANILLA_DYES) {
                if (entryPath.contains(vanillaColor.getName())) {
                    ((DyeBlockStorage) block).setDyeColor(vanillaColor);

                    break;
                }
            }
        }
    }

}
