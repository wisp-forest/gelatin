package io.wispforest.jello;

import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.item.RecipeSpecificRemainders;
import io.wispforest.jello.api.util.TrackedDataHandlerExtended;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.blockentity.JelloBlockEntityTypes;
import io.wispforest.jello.client.render.screen.ColorMixerScreenHandler;
import io.wispforest.jello.client.render.screen.JelloScreenHandlerTypes;
import io.wispforest.jello.data.loot.JelloLootTables;
import io.wispforest.jello.misc.JelloGameEvents;
import io.wispforest.jello.misc.JelloStats;
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
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Jello implements ModInitializer {

    public static final String MODID = "jello";

    private static JelloConfig MAIN_CONFIG;
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(new Identifier(MODID, "main"));

    @Override
    public void onInitialize() {

        //API Stuff
        DyeColorantRegistry.initVanillaDyes();

        JelloLootTables.registerLootTablesGeneration();

        JelloCauldronBehaviors.registerJelloBehaviorBypass();

        //-----------------------------------------------------

        RecipeSpecificRemainders.add(Jello.id("sponge_item_from_wet_sponge"), Items.SHEARS);
        RecipeSpecificRemainders.add(Jello.id("sponge_item_from_dry_sponge"), Items.SHEARS);
        RecipeSpecificRemainders.add(Jello.id("artist_palette"), Items.SHEARS);

        JelloScreenHandlerTypes.initialize();

        AutoConfig.register(JelloConfig.class, GsonConfigSerializer::new);
        MAIN_CONFIG = AutoConfig.getConfigHolder(JelloConfig.class).getConfig();

        // Block Registry
        FieldRegistrationHandler.register(JelloBlocks.class, MODID, false);

        // Block Entity's
        FieldRegistrationHandler.register(JelloBlockEntityTypes.class, MODID, false);

        // Item Registry
        FieldRegistrationHandler.register(JelloItems.class, MODID, true);

        FieldRegistrationHandler.register(JelloRecipeSerializers.class, MODID, false);
        FieldRegistrationHandler.register(JelloGameEvents.class, MODID, false);

        FieldRegistrationHandler.processSimple(TrackedDataHandlerExtended.class, false);
        FieldRegistrationHandler.processSimple(JelloStats.class, false);

        //TODO: Add warning when connecting to a server with just disabled or when the client connects with it disabled
        if(getConfig().addCustomJsonColors) {
            DyeColorantLoader.loadFromJson();
        }
        DyeColorantRegistry.registerModidModelRedirect(Jello.MODID);

        ((OwoItemGroup) ItemGroup.MISC).initialize();

        registerDispenserBehavior();

        JelloBlockVariants.initialize();

        initializeNetworking();
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

}
