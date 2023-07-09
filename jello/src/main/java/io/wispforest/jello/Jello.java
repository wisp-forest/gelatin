package io.wispforest.jello;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.wispforest.gelatin.common.events.CauldronEvent;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.blockentity.JelloBlockEntityTypes;
import io.wispforest.jello.client.gui.dyebundle.DyeBundleTooltipBuilder;
import io.wispforest.jello.client.gui.screen.ColorMixerScreenHandler;
import io.wispforest.jello.client.gui.screen.JelloScreenHandlerTypes;
import io.wispforest.jello.compat.JelloConfig;
import io.wispforest.jello.data.recipe.JelloRecipeSerializers;
import io.wispforest.jello.misc.ColorDebugHelper;
import io.wispforest.jello.misc.DyeColorantLoader;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.SpongeItem;
import io.wispforest.jello.item.dyebundle.DyeBundlePackets;
import io.wispforest.jello.misc.JelloBlockVariants;
import io.wispforest.jello.misc.JelloPotions;
import io.wispforest.jello.network.ColorMixerScrollPacket;
import io.wispforest.jello.network.ColorMixerSearchPacket;
import io.wispforest.jello.network.CustomJsonColorSync;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.util.RecipeRemainderStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Jello implements ModInitializer {

    public static final String MODID = "jello";

    private static JelloConfig MAIN_CONFIG = null;

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

        FieldRegistrationHandler.register(JelloPotions.class, MODID, false);

        FieldRegistrationHandler.register(JelloItems.class, MODID, true);

        BrewingRecipeRegistry.registerPotionRecipe(Potions.REGENERATION, JelloItems.CONCENTRATED_DRAGON_BREATH, JelloPotions.DRAGON_HEALTH);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.LONG_REGENERATION, JelloItems.CONCENTRATED_DRAGON_BREATH, JelloPotions.LONG_DRAGON_HEALTH);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.STRONG_REGENERATION, JelloItems.CONCENTRATED_DRAGON_BREATH, JelloPotions.STRONG_DRAGON_HEALTH);

        BrewingRecipeRegistry.registerPotionRecipe(JelloPotions.DRAGON_HEALTH, Items.REDSTONE, JelloPotions.LONG_DRAGON_HEALTH);
        BrewingRecipeRegistry.registerPotionRecipe(JelloPotions.DRAGON_HEALTH, Items.GLOWSTONE_DUST, JelloPotions.STRONG_DRAGON_HEALTH);

        FieldRegistrationHandler.register(JelloRecipeSerializers.class, MODID, false);

        FieldRegistrationHandler.register(JelloScreenHandlerTypes.class, MODID, false);

        JelloBlockVariants.initialize();

        //---------------------------------------------------------------------------------


        //---------------------------------------------------------------------------------

        RecipeRemainderStorage.store(Jello.id("sponge_item_from_wet_sponge"), Map.of(Items.SHEARS, Items.SHEARS.getDefaultStack()));
        RecipeRemainderStorage.store(Jello.id("sponge_item_from_dry_sponge"), Map.of(Items.SHEARS, Items.SHEARS.getDefaultStack()));
        RecipeRemainderStorage.store(Jello.id("artist_palette"), Map.of(Items.SHEARS, Items.SHEARS.getDefaultStack()));

        if(Jello.getConfig().addCustomJsonColors()) DyeColorantLoader.loadFromJson();

        initializeNetworking();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("generate_color_build")
                            .then(
                                    CommandManager.argument("type", StringArgumentType.string())
                                            .suggests((context, builder) -> CommandSource.suggestMatching(List.of("delete", "cube"), builder))
                                                    .then(
                                                            CommandManager.argument("cube_size", IntegerArgumentType.integer(2))
                                                                    .executes(context ->
                                                                            runDebugCommand(context,
                                                                                    StringArgumentType.getString(context, "type"),
                                                                                    IntegerArgumentType.getInteger(context, "cube_size"))
                                                                    )
                                                    )
                                                    .executes(context ->
                                                            runDebugCommand(context,
                                                                    StringArgumentType.getString(context, "type"),
                                                                    5)
                                                    )
                            )
            );
        });
    }

    private static int runDebugCommand(CommandContext<ServerCommandSource> context, String type, int cubeSize){
        Vec3d position = context.getSource().getPosition();

        if(Objects.equals(type, "delete")){
            ColorDebugHelper.INSTANCE.clearLastWorkspace(context.getSource().getWorld());
        } else {
            ColorDebugHelper.INSTANCE.runBuilderProgram(
                    new BlockPos(Math.round(position.x), Math.round(position.y), Math.round(position.z)),
                    context.getSource().getWorld(),
                    cubeSize,
                    1
            );
        }

        return 0;
    }

    //------------------------------------------------------------------------------

    private static void initializeNetworking() {
        CHANNEL.registerServerbound(DyeBundlePackets.ScrollGivenBundle.class, DyeBundlePackets.ScrollGivenBundle::scrollBundle);

        CHANNEL.registerServerbound(DyeBundlePackets.StartStackTracking.class, DyeBundlePackets.StartStackTracking::startTracking);

        CHANNEL.registerServerbound(DyeBundlePackets.DyeBundleStackInteraction.class, DyeBundlePackets.DyeBundleStackInteraction::interact);

        CHANNEL.registerClientboundDeferred(DyeBundleTooltipBuilder.UpdateDyeBundleTooltip.class);

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

    public static JelloConfig getConfig() {
        if(MAIN_CONFIG == null) MAIN_CONFIG = JelloConfig.createAndLoad();

        return MAIN_CONFIG;
    }

    //TODO: GET WORKING AGAIN WITHIN THE FUTURE
//    private static void ColorBlockRegistryCompat() {
//        ModCompatHelpers.getRegistryHelper(Registry.ITEM).runWhenPresent(new Identifier(ConsistencyPlusTags.CONSISTENCY_PLUS_MODID, "stone_wall"), item -> ConsistencyPlusColorBlockRegistry.init());
//    }

    //------------------------------------------------------------------------------

}
