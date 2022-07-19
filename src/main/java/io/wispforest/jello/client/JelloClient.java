package io.wispforest.jello.client;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableVariantManager;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.item.DyeableItemVariant;
import io.wispforest.jello.api.events.HotbarMouseEvents;
import io.wispforest.jello.api.events.TranslationInjectionEvent;
import io.wispforest.jello.api.registry.ColorizeBlackListRegistry;
import io.wispforest.jello.api.registry.GrayScaleRegistry;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.block.colored.ColoredGlassBlock;
import io.wispforest.jello.block.colored.ColoredGlassPaneBlock;
import io.wispforest.jello.client.render.DyeBundleTooltipRender;
import io.wispforest.jello.client.render.screen.ColorMixerScreen;
import io.wispforest.jello.client.render.screen.JelloScreenHandlerTypes;
import io.wispforest.jello.compat.JelloConfig;
import io.wispforest.jello.data.providers.JelloLangProvider;
import io.wispforest.jello.item.JelloDyeItem;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.SpongeItem;
import io.wispforest.jello.item.dyebundle.DyeBundleScreenEvent;
import io.wispforest.jello.misc.dye.JelloBlockVariants;
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
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class JelloClient implements ClientModInitializer {

    public static final Identifier BED_BLANKET_ONLY = Jello.id("block/bed/blanket_only");
    public static final Identifier BED_PILLOW_ONLY = Jello.id("block/bed/pillow_only");

    private static final RenderLayer TRANSLUCENT = RenderLayer.getTranslucent();

    @Override
    public void onInitializeClient() {

        Jello.MAIN_ITEM_GROUP.initialize();

        //----------------------------[Independent Api Stuff's]----------------------------

        if (FabricLoader.getInstance().isModLoaded("continuity")) {
            FabricLoader.getInstance().getModContainer(Jello.MODID).ifPresent(container -> {
                ResourceManagerHelper.registerBuiltinResourcePack(Jello.id("continuity_comp"), container, ResourcePackActivationType.NORMAL);
            });
        }

        FabricLoader.getInstance().getModContainer(Jello.MODID).ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(Jello.id("cauldron_cull_fix"), container, ResourcePackActivationType.DEFAULT_ENABLED);
        });

        JelloClient.registerColorProvidersForBlockVariants();

        ClientSpriteRegistryCallback.event(TexturedRenderLayers.BEDS_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(JelloClient.BED_BLANKET_ONLY);
            registry.register(JelloClient.BED_PILLOW_ONLY);
        });

        JelloClient.clientEventRegistry();

        //----------------------------------------------------------------------------------


        //-------------------------------[Other Block Stuff's]------------------------------

        ScreenRegistry.register(JelloScreenHandlerTypes.COLOR_MIXER_TYPE, ColorMixerScreen::new);

        registerRenderLayersForJelloBlockVariants();

        registerCauldronSpecificLayerAndColor();

        //----------------------------------------------------------------------------------


        //--------------------------------[Other Item Stuff's]------------------------------

        JelloItems.Slimeballs.SLIME_BALLS.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider) item, item));

        JelloItems.JelloCups.JELLO_CUP.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider) item, item));

        ColorProviderRegistry.ITEM.register((ItemColorProvider) JelloItems.ARTIST_PALETTE, JelloItems.ARTIST_PALETTE);

        FabricModelPredicateProviderRegistry.register(JelloItems.SPONGE, new Identifier("dirtiness"), (stack, world, entity, seed) -> SpongeItem.getDirtinessStage(stack));

        FabricModelPredicateProviderRegistry.register(JelloItems.DYE_BUNDLE, new Identifier("filled"), (stack, world, entity, seed) -> BundleItem.getAmountFilled(stack));

        //----------------------------------------------------------------------------------

        Jello.MAIN_CONFIG.registerSaveListener((configHolder, jelloConfig) -> {
            GrayScaleRegistry.GRAYSCALABLE_MODID_BLACKLIST.addAll(jelloConfig.grayScaledBlackListModid);
            ColorizeBlackListRegistry.MODID_BLACKLIST.addAll(jelloConfig.grayScaledBlackListModid);

            toggleRenderLayer(jelloConfig);

            return ActionResult.SUCCESS;
        });

        Jello.MAIN_CONFIG.registerLoadListener((configHolder, jelloConfig) -> {
            GrayScaleRegistry.GRAYSCALABLE_MODID_BLACKLIST.addAll(jelloConfig.grayScaledBlackListModid);
            ColorizeBlackListRegistry.MODID_BLACKLIST.addAll(jelloConfig.grayScaledBlackListModid);

            toggleRenderLayer(jelloConfig);

            return ActionResult.SUCCESS;
        });
    }

    //------------------------------------------------------------------------------

    private static void clientEventRegistry() {
        HudRenderCallback.EVENT.register(new DyeBundleTooltipRender());

        HotbarMouseEvents.ALLOW_MOUSE_SCROLL.register(
                (player, horizontalAmount, verticalAmount) -> new DyeBundleScreenEvent().allowMouseScroll(player, horizontalAmount, verticalAmount));

        ClientLoginNetworking.registerGlobalReceiver(Jello.id("json_color_sync"), (client, handler, buf, listenerAdder) -> {
            PacketByteBuf buffer = PacketByteBufs.create();

            buffer.writeBoolean(Jello.getConfig().addCustomJsonColors);

            return CompletableFuture.completedFuture(buffer);
        });

        TranslationInjectionEvent.AFTER_LANGUAGE_LOAD.register(helper -> {
            for (DyeableVariantManager.DyeColorantVariantData dyedVariant : DyeableVariantManager.getVariantMap().values()) {
                for (Block block : dyedVariant.dyedBlocks().values()) {
                    helper.addBlock(block);
                }

                for (Item item : dyedVariant.dyedItems().values()) {
                    helper.addItem(item);
                }
            }

            DyeableBlockVariant.getAllBlockVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.alwaysReadOnly() && dyeableBlockVariant.createBlockItem()).forEach(dyeableBlockVariant -> {
                helper.addTranslation(dyeableBlockVariant.variantIdentifier.getPath() + "_condensed", JelloLangProvider.titleFormatString(dyeableBlockVariant.variantIdentifier.getPath().split("_"), true));
            });

            DyeableItemVariant.getAllItemVariants().stream().filter(dyeableItemVariant -> !dyeableItemVariant.alwaysReadOnly()).forEach(dyeableItemVariant -> {
                helper.addTranslation(dyeableItemVariant.variantIdentifier.getPath() + "_condensed", JelloLangProvider.titleFormatString(dyeableItemVariant.variantIdentifier.getPath().split("_"), true));
            });
        });
    }

    //-------------------------------------------------------------------------------------

    public static void registerCauldronSpecificLayerAndColor(){
        if(Jello.getConfig().enableTransparencyFixCauldrons) {
            registerBlockLayer(Blocks.WATER_CAULDRON, TRANSLUCENT);
        }

        ColorProviderRegistry.BLOCK.register((BlockColorProvider) Blocks.WATER_CAULDRON, Blocks.WATER_CAULDRON);
    }

    public static void registerColorProvidersForBlockVariants() {
        for (Map.Entry<DyeColorant, DyeableVariantManager.DyeColorantVariantData> dyedContainerEntry : DyeableVariantManager.getVariantMap().entrySet()) {
            for (Map.Entry<DyeableBlockVariant, Block> blockVariantEntry : dyedContainerEntry.getValue().dyedBlocks().entrySet()) {
                Block block = blockVariantEntry.getValue();

                // Remove blocks that are being handled by Minecraft i.e Vanilla blocks
                if(!Objects.equals(Registry.BLOCK.getId(block).getNamespace(), "minecraft")) {

                    //Read only block Variants are handled by the mod inwhich add them and remove any blocks that don't have Color providers
                    if (!blockVariantEntry.getKey().alwaysReadOnly() && block instanceof BlockColorProvider) {
                        if (block instanceof ColoredGlassBlock || block instanceof ColoredGlassPaneBlock) {
                            registerBlockLayer(block, RenderLayer.getTranslucent());
                            registerBlockItemLayer(block.asItem(), RenderLayer.getTranslucent());
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
                Item dyeItem = dyedContainerEntry.getValue().dyeItem();

                ColorProviderRegistry.ITEM.register((JelloDyeItem)dyeItem, dyeItem);

                FabricModelPredicateProviderRegistry.register(dyeItem, new Identifier("variant"), (stack, world, entity, seed) -> JelloDyeItem.getTextureVariant(stack));
            }
        }
    }

    private static void registerRenderLayersForJelloBlockVariants(){
        registerBlockLayer(JelloBlocks.SLIME_SLAB, TRANSLUCENT);
        registerBlockItemLayer(JelloBlocks.SLIME_SLAB.asItem(), TRANSLUCENT);

        for(DyeColorant dyeColorant : DyeColorantRegistry.DYE_COLOR.stream().toList()){
            registerBlockLayer(JelloBlockVariants.SLIME_BLOCK.getColoredEntry(dyeColorant), RenderLayer.getTranslucent());
            registerBlockItemLayer(JelloBlockVariants.SLIME_BLOCK.getColoredEntry(dyeColorant).asItem(), RenderLayer.getTranslucent());

            registerBlockLayer(JelloBlockVariants.SLIME_SLAB.getColoredEntry(dyeColorant), RenderLayer.getTranslucent());
            registerBlockItemLayer(JelloBlockVariants.SLIME_SLAB.getColoredEntry(dyeColorant).asItem(), RenderLayer.getTranslucent());
        }
    }

    private static void toggleRenderLayer(JelloConfig jelloConfig){
        if(jelloConfig.enableTransparencyFixCauldrons) {
            registerBlockLayer(Blocks.WATER_CAULDRON, TRANSLUCENT);
        } else {
            registerBlockLayer(Blocks.WATER_CAULDRON, RenderLayer.getSolid());
        }

        MinecraftClient.getInstance().worldRenderer.reload();
    }

    private static void registerBlockLayer(Block block, RenderLayer renderLayer){
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer);
    }

    private static void registerBlockItemLayer(Item blockItem, RenderLayer renderLayer){
        BlockRenderLayerMap.INSTANCE.putItem(blockItem, renderLayer);
    }

    //-------------------------------------------------------------------------------------
}
