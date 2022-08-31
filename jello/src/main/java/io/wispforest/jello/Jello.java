package io.wispforest.jello;

import io.wispforest.common.CommonInit;
import io.wispforest.common.events.CauldronEvent;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.blockentity.JelloBlockEntityTypes;
import io.wispforest.jello.client.render.screen.ColorMixerScreenHandler;
import io.wispforest.jello.client.render.screen.JelloScreenHandlerTypes;
import io.wispforest.jello.data.recipe.JelloRecipeSerializers;
import io.wispforest.jello.misc.DyeColorantLoader;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.SpongeItem;
import io.wispforest.jello.item.dyebundle.DyeBundlePackets;
import io.wispforest.jello.misc.JelloBlockVariants;
import io.wispforest.jello.network.ColorMixerScrollPacket;
import io.wispforest.jello.network.ColorMixerSearchPacket;
import io.wispforest.jello.network.CustomJsonColorSync;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.util.RecipeRemainderStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Map;

public class Jello implements ModInitializer {

    public static final String MODID = "jello";

    public static Identifier id(String path){
        return new Identifier(MODID, path);
    }

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(new Identifier(MODID, "main"));

    public static final boolean DEBUG_ENV_VAR = Boolean.getBoolean("jello.debug");
    public static final boolean DEBUG_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();

    @Override
    public void onInitialize() {

        CauldronEvent.BEFORE_CAULDRON_BEHAVIOR.register(SpongeItem::cleanSponge);

        //-------------------------------[Entry Registry's]--------------------------------
        FieldRegistrationHandler.register(JelloBlocks.class, MODID, false);

        FieldRegistrationHandler.register(JelloBlockEntityTypes.class, MODID, false);

        FieldRegistrationHandler.register(JelloItems.class, MODID, true);

        FieldRegistrationHandler.register(JelloRecipeSerializers.class, MODID, false);

//        FieldRegistrationHandler.register(JelloGameEvents.class, MODID, false);

        FieldRegistrationHandler.register(JelloScreenHandlerTypes.class, MODID, false);

//        FieldRegistrationHandler.processSimple(TrackedDataHandlerExtended.class, false);

//        FieldRegistrationHandler.processSimple(JelloStats.class, false);

//        JelloBlockVariants.initialize();

        JelloBlockVariants.initialize();
        //---------------------------------------------------------------------------------


        //---------------------------------------------------------------------------------

        RecipeRemainderStorage.store(Jello.id("sponge_item_from_wet_sponge"), Map.of(Items.SHEARS, Items.SHEARS.getDefaultStack()));
        RecipeRemainderStorage.store(Jello.id("sponge_item_from_dry_sponge"), Map.of(Items.SHEARS, Items.SHEARS.getDefaultStack()));
        RecipeRemainderStorage.store(Jello.id("artist_palette"), Map.of(Items.SHEARS, Items.SHEARS.getDefaultStack()));

        if(CommonInit.getConfig().addCustomJsonColors) {
            DyeColorantLoader.loadFromJson();
        }

        initializeNetworking();
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
            sender.sendPacket(Jello.id("json_color_sync"), PacketByteBufs.create());
        });

        ServerLoginNetworking.registerGlobalReceiver(Jello.id("json_color_sync"), (server, handler, understood, buf, synchronizer, responseSender) -> {
            CustomJsonColorSync.confirmMatchingConfigOptions(buf.getBoolean(0), handler);
        });

    }

    //TODO: GET WORKING AGAIN WITHIN THE FUTURE
//    private static void ColorBlockRegistryCompat() {
//        ModCompatHelpers.getRegistryHelper(Registry.ITEM).runWhenPresent(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, "stone_wall"), item -> ConsistencyPlusColorBlockRegistry.init());
//    }

    //------------------------------------------------------------------------------

}
