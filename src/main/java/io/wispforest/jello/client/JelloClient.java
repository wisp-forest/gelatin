package io.wispforest.jello.client;

import io.netty.buffer.ByteBuf;
import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.api.events.HotbarMouseEvents;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.block.SlimeBlockColored;
import io.wispforest.jello.block.SlimeSlabColored;
import io.wispforest.jello.block.colored.ColoredGlassBlock;
import io.wispforest.jello.block.colored.ColoredGlassPaneBlock;
import io.wispforest.jello.client.render.DyeBundleTooltipRender;
import io.wispforest.jello.client.render.screen.ColorMixerScreen;
import io.wispforest.jello.client.render.screen.JelloScreenHandlerTypes;
import io.wispforest.jello.item.JelloDyeItem;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.SpongeItem;
import io.wispforest.jello.item.dyebundle.DyeBundleScreenEvent;
import io.wispforest.jello.network.CustomJsonColorSync;
import io.wispforest.owo.network.serialization.RecordSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.item.BundleItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class JelloClient implements ClientModInitializer {

    public static final Identifier BED_BLANKET_ONLY = Jello.id("block/bed/blanket_only");
    public static final Identifier BED_PILLOW_ONLY = Jello.id("block/bed/pillow_only");
    
    @Override
    public void onInitializeClient() {

        Jello.MAIN_ITEM_GROUP.initialize();

        //  Api Stuff

        // Resource Pack loading

        if (FabricLoader.getInstance().isModLoaded("continuity")) {
            FabricLoader.getInstance().getModContainer(Jello.MODID).ifPresent(container -> {
                ResourceManagerHelper.registerBuiltinResourcePack(Jello.id("continuity_comp"), container, ResourcePackActivationType.NORMAL);
            });
        }

        FabricLoader.getInstance().getModContainer(Jello.MODID).ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(Jello.id("cauldron_cull_fix"), container, ResourcePackActivationType.DEFAULT_ENABLED);
        });

        //-----------------------------------------------------------------------------------------

        JelloClient.registerColorProvidersForBlockVariants();

        ClientSpriteRegistryCallback.event(TexturedRenderLayers.BEDS_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(JelloClient.BED_BLANKET_ONLY);
            registry.register(JelloClient.BED_PILLOW_ONLY);
        });

        ColorProviderRegistry.BLOCK.register((BlockColorProvider) Blocks.WATER_CAULDRON, Blocks.WATER_CAULDRON);

        JelloClient.clientEventRegistry();

        //-----------------------------------------------------------------------------------------

        if(Jello.getConfig().enableTransparencyFixCauldrons) {
            BlockRenderLayerMap.INSTANCE.putBlock(Blocks.WATER_CAULDRON, RenderLayer.getTranslucent());
        }

        ScreenRegistry.register(JelloScreenHandlerTypes.COLOR_MIXER_TYPE, ColorMixerScreen::new);

        slimeBlockClientInit();

        jelloItemInit();

        registerCustomModelPredicate();
    }

    //------------------------------------------------------------------------------

    private static void slimeBlockClientInit() {
        BlockRenderLayerMap.INSTANCE.putBlock(JelloBlocks.SLIME_SLAB, RenderLayer.getTranslucent());

        for (Map.Entry<DyeColorant, DyedVariantContainer> dyedVariantEntry : DyedVariantContainer.getVariantMap().entrySet()) {
            for (Block block : dyedVariantEntry.getValue().dyedBlocks.values()) {
                if (block instanceof SlimeBlockColored || block instanceof SlimeSlabColored) {
//                    ColorProviderRegistry.BLOCK.register((BlockColorProvider) block, block);
//                    ColorProviderRegistry.ITEM.register((ItemColorProvider) block.asItem(), block.asItem());

                    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
                }
            }
        }
    }

    private static void jelloItemInit() {
        JelloItems.Slimeballs.SLIME_BALLS.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider) item, item));

        JelloItems.JelloCups.JELLO_CUP.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider) item, item));

        ColorProviderRegistry.ITEM.register((ItemColorProvider) JelloItems.ARTIST_PALETTE, JelloItems.ARTIST_PALETTE);
    }

    //-------------------------------------------------------------------------------------

    private static void registerCustomModelPredicate() {
        FabricModelPredicateProviderRegistry.register(JelloItems.SPONGE, new Identifier("dirtiness"), (stack, world, entity, seed) -> SpongeItem.getDirtinessStage(stack));

        FabricModelPredicateProviderRegistry.register(JelloItems.DYE_BUNDLE, new Identifier("filled"), (stack, world, entity, seed) -> BundleItem.getAmountFilled(stack));
    }

    //-------------------------------------------------------------------------------------

    private static void clientEventRegistry() {
        HudRenderCallback.EVENT.register(new DyeBundleTooltipRender());

        HotbarMouseEvents.ALLOW_MOUSE_SCROLL.register(
                (player, horizontalAmount, verticalAmount) -> new DyeBundleScreenEvent().allowMouseScroll(player, horizontalAmount, verticalAmount));

        ClientLoginNetworking.registerGlobalReceiver(Jello.id("json_color_sync"), (client, handler, buf, listenerAdder) -> {
            PacketByteBuf buffer = PacketByteBufs.create();

            buffer.writeBoolean(Jello.getConfig().addCustomJsonColors);

            return CompletableFuture.completedFuture(buffer);
        });
    }

    public static void registerColorProvidersForBlockVariants() {
        for (Map.Entry<DyeColorant, DyedVariantContainer> dyedContainerEntry : DyedVariantContainer.getVariantMap().entrySet()) {
            for (Map.Entry<DyeableBlockVariant, Block> blockVariantEntry : dyedContainerEntry.getValue().dyedBlocks.entrySet()) {
                Block block = blockVariantEntry.getValue();

                // Remove blocks that are being handled by Minecraft i.e Vanilla blocks
                if(!Objects.equals(Registry.BLOCK.getId(block).getNamespace(), "minecraft")) {

                    //Read only block Variants are handled by the mod inwhich add them and remove any blocks that don't have Color providers
                    if (!blockVariantEntry.getKey().alwaysReadOnly() && block instanceof BlockColorProvider) {
                        if (block instanceof ColoredGlassBlock || block instanceof ColoredGlassPaneBlock) {
                            BlockRenderLayerMapImpl.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
                            BlockRenderLayerMapImpl.INSTANCE.putItem(block.asItem(), RenderLayer.getTranslucent());
                        } else if (block instanceof ShulkerBoxBlock) {
                            BuiltinItemRendererRegistry.INSTANCE.register(block, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                                ShulkerBoxBlockEntity shulkerBoxBlockEntity = new ShulkerBoxBlockEntity(DyeColorantRegistry.Constants.NULL_VALUE_OLD, BlockPos.ORIGIN, block.getDefaultState());

                                MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(shulkerBoxBlockEntity).render(shulkerBoxBlockEntity, 0.0F, matrices, vertexConsumers, light, overlay);
                            });
                        }

                        ColorProviderRegistry.BLOCK.register((BlockColorProvider) block, block);

                        if (blockVariantEntry.getKey().createBlockItem() && block.asItem() instanceof ItemColorProvider) {
                            ColorProviderRegistry.ITEM.register((ItemColorProvider) block.asItem(), block.asItem());
                        }
                    }
                }
            }

            //-----------------------------------------------------------

            // Stop client side registry from touching Minecraft Dyes
            if(!Objects.equals(dyedContainerEntry.getKey().getId().getNamespace(), "minecraft")) {
                ColorProviderRegistry.ITEM.register((JelloDyeItem) dyedContainerEntry.getValue().dyeItem, dyedContainerEntry.getValue().dyeItem);

                FabricModelPredicateProviderRegistry.register(dyedContainerEntry.getValue().dyeItem, new Identifier("variant"), (stack, world, entity, seed) -> JelloDyeItem.getTextureVariant(stack));
            }
        }
    }
}
