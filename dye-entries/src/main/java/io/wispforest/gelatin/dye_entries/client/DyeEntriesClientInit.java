package io.wispforest.gelatin.dye_entries.client;

import io.wispforest.gelatin.common.data.providers.ImplLangProvider;
import io.wispforest.gelatin.common.events.TranslationInjectionEvent;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entries.DyeEntriesInit;
import io.wispforest.gelatin.dye_entries.block.ColoredGlassBlock;
import io.wispforest.gelatin.dye_entries.block.ColoredGlassPaneBlock;
import io.wispforest.gelatin.dye_entries.item.GelatinDyeItem;
import io.wispforest.gelatin.dye_entries.utils.GrayScaleBlockRegistry;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantManager;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_entries.variants.item.DyeableItemVariant;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
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
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Objects;

public class DyeEntriesClientInit implements ClientModInitializer {

    public static final Identifier BED_BLANKET_ONLY = GelatinConstants.id("block/bed/blanket_only");
    public static final Identifier BED_PILLOW_ONLY = GelatinConstants.id("block/bed/pillow_only");

    private static final RenderLayer TRANSLUCENT = RenderLayer.getTranslucent();

    @Override
    public void onInitializeClient() {
        DyeEntriesInit.MAIN_ITEM_GROUP.initialize();

        if (FabricLoader.getInstance().isModLoaded("continuity")) {
            FabricLoader.getInstance().getModContainer("dye_block_and_item").ifPresent(container -> {
                ResourceManagerHelper.registerBuiltinResourcePack(GelatinConstants.id("continuity_comp"), container, ResourcePackActivationType.NORMAL);
            });
        }

        registerColorProvidersForBlockVariants();

        ClientSpriteRegistryCallback.event(TexturedRenderLayers.BEDS_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(BED_BLANKET_ONLY);
            registry.register(BED_PILLOW_ONLY);
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
                helper.addTranslation(dyeableBlockVariant.variantIdentifier.getPath() + "_condensed", ImplLangProvider.titleFormatString(dyeableBlockVariant.variantIdentifier.getPath().split("_"), true));
            });

            DyeableItemVariant.getAllItemVariants().stream().filter(dyeableItemVariant -> !dyeableItemVariant.alwaysReadOnly()).forEach(dyeableItemVariant -> {
                helper.addTranslation(dyeableItemVariant.variantIdentifier.getPath() + "_condensed", ImplLangProvider.titleFormatString(dyeableItemVariant.variantIdentifier.getPath().split("_"), true));
            });
        });

        GrayScaleBlockRegistry.register(Blocks.GLOWSTONE);
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

                ColorProviderRegistry.ITEM.register((GelatinDyeItem)dyeItem, dyeItem);

                ModelPredicateProviderRegistry.register(dyeItem, new Identifier("variant"), (stack, world, entity, seed) -> GelatinDyeItem.getTextureVariant(stack));
            }
        }
    }



    private static void registerBlockLayer(Block block, RenderLayer renderLayer){
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer);
    }

    private static void registerBlockItemLayer(Item blockItem, RenderLayer renderLayer){
        BlockRenderLayerMap.INSTANCE.putItem(blockItem, renderLayer);
    }
}
