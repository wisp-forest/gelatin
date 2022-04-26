package io.wispforest.jello.client;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Objects;

public class JelloClient implements ClientModInitializer {

    public static final Identifier BED_BLANKET_ONLY = Jello.id("block/bed/blanket_only");
    public static final Identifier BED_PILLOW_ONLY = Jello.id("block/bed/pillow_only");
    
    @Override
    public void onInitializeClient() {
        //  Api Stuff

        if (FabricLoader.getInstance().isModLoaded("continuity")) {
            FabricLoader.getInstance().getModContainer(Jello.MODID).ifPresent(container -> {
                ResourceManagerHelper.registerBuiltinResourcePack(Jello.id("continuity_comp"), container, ResourcePackActivationType.NORMAL);
            });
        }

        JelloClient.registerJsonBlocksForColor();

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
                    ColorProviderRegistry.BLOCK.register((BlockColorProvider) block, block);
                    ColorProviderRegistry.ITEM.register((ItemColorProvider) block.asItem(), block.asItem());

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

    }

    public static void registerJsonBlocksForColor() {
        for (Map.Entry<DyeColorant, DyedVariantContainer> dyedVariantEntry : DyedVariantContainer.getVariantMap().entrySet()) {
            if (!Objects.equals(dyedVariantEntry.getKey().getId().getNamespace(), "minecraft")) {
                for (Block block : dyedVariantEntry.getValue().dyedBlocks.values()) {
                    if (block instanceof ColoredGlassBlock || block instanceof ColoredGlassPaneBlock) {
                        ColorProviderRegistry.BLOCK.register((BlockColorProvider) block, block);
                        ColorProviderRegistry.ITEM.register((ItemColorProvider) block.asItem(), block.asItem());

                        BlockRenderLayerMapImpl.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
                        BlockRenderLayerMapImpl.INSTANCE.putItem(block.asItem(), RenderLayer.getTranslucent());
                    } else if (block instanceof ShulkerBoxBlock) {
                        BuiltinItemRendererRegistry.INSTANCE.register(block, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                            ShulkerBoxBlockEntity shulkerBoxBlockEntity = new ShulkerBoxBlockEntity(DyeColorantRegistry.Constants.NULL_VALUE_OLD, BlockPos.ORIGIN, block.getDefaultState());

                            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(shulkerBoxBlockEntity).render(shulkerBoxBlockEntity, 0.0F, matrices, vertexConsumers, light, overlay);
                        });
                    }
//                    else if (block instanceof BedBlock) {
//                        BuiltinItemRendererRegistry.INSTANCE.register(block, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
//                            BedBlockEntity renderBed = new BedBlockEntity(BlockPos.ORIGIN, block.getDefaultState());
//
//                            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(renderBed).render(renderBed, 0.0F, matrices, vertexConsumers, light, overlay);
//                        });
//                    }
                    else {
                        ColorProviderRegistry.BLOCK.register((BlockColorProvider) block, block);

                        Item item = block.asItem();

                        if (item != Blocks.AIR.asItem()) {
                            ColorProviderRegistry.ITEM.register((ItemColorProvider) item, item);
                        }
                    }
                }

                //-----------------------------------------------------------

                ColorProviderRegistry.ITEM.register((JelloDyeItem) dyedVariantEntry.getValue().dyeItem, dyedVariantEntry.getValue().dyeItem);

                FabricModelPredicateProviderRegistry.register(dyedVariantEntry.getValue().dyeItem, new Identifier("variant"), (stack, world, entity, seed) -> JelloDyeItem.getTextureVariant(stack));

            }
        }
    }
}
