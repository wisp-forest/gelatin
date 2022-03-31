package io.wispforest.jello.api;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.item.JelloDyeItem;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.Jello;
import io.wispforest.jello.block.colored.ColoredGlassBlock;
import io.wispforest.jello.block.colored.ColoredGlassPaneBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
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
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JelloAPIClient implements ClientModInitializer {

    public static final Identifier BED_BLANKET_ONLY = Jello.id("block/bed/blanket_only");
    public static final Identifier BED_PILLOW_ONLY = Jello.id("block/bed/pillow_only");

    private static final Set<DyeableBlockVariant> ALL_VARIANTS = new HashSet<>();

    @Override
    public void onInitializeClient() {
        if (ALL_VARIANTS.isEmpty()) {
            for (DyeableBlockVariant dyeableBlockVariant : VanillaBlockVariants.VANILLA_VARIANTS) {
                addToListWithRecursion(dyeableBlockVariant);
            }

            for (DyeableBlockVariant dyeableBlockVariant : DyeableBlockVariant.ADDITION_BLOCK_VARIANTS) {
                addToListWithRecursion(dyeableBlockVariant);
            }
        }


        if (FabricLoader.getInstance().isModLoaded("continuity")) {
            FabricLoader.getInstance().getModContainer(Jello.MODID).ifPresent(container -> {
                ResourceManagerHelper.registerBuiltinResourcePack(Jello.id("continuity_comp"), container, ResourcePackActivationType.NORMAL);
            });
        }

        ColorProviderRegistry.BLOCK.register((BlockColorProvider) Blocks.WATER_CAULDRON, Blocks.WATER_CAULDRON);
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.WATER_CAULDRON, RenderLayer.getTranslucent());

        initJsonDyeItems();

        registerJsonBlocksForColor();

        ClientSpriteRegistryCallback.event(TexturedRenderLayers.BEDS_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(BED_BLANKET_ONLY);
            registry.register(BED_PILLOW_ONLY);
        });
    }

    public static void addToListWithRecursion(DyeableBlockVariant parentBlockVariant) {
        ALL_VARIANTS.add(parentBlockVariant);

        if (parentBlockVariant.childVariant != null) {
            addToListWithRecursion(parentBlockVariant.childVariant.get());
        }
    }

    //------------------------------------------------------------------------------

    private static void initJsonDyeItems() {
//        ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> {
//            return new DyeModelResourceRedirect();
//        });
//
//        ModelLoadingRegistry.INSTANCE.registerVariantProvider(resourceManager -> {
//            return new BlockModelRedirect();
//        });
    }


    private static void registerJsonBlocksForColor() {
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
                    } else if (block instanceof BedBlock) {
                        BuiltinItemRendererRegistry.INSTANCE.register(block, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                            BedBlockEntity renderBed = new BedBlockEntity(BlockPos.ORIGIN, block.getDefaultState());

                            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(renderBed).render(renderBed, 0.0F, matrices, vertexConsumers, light, overlay);
                        });
                    } else {
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

    //------------------------------------------------------------------------------

}
