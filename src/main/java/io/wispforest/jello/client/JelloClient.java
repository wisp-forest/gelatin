package io.wispforest.jello.client;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.api.events.HotbarMouseEvents;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.block.SlimeBlockColored;
import io.wispforest.jello.block.SlimeSlabColored;
import io.wispforest.jello.client.render.DyeBundleTooltipRender;
import io.wispforest.jello.client.render.screen.ColorMixerScreen;
import io.wispforest.jello.client.render.screen.JelloScreenHandlerTypes;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.SpongeItem;
import io.wispforest.jello.item.dyebundle.DyeBundleScreenEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BundleItem;
import net.minecraft.util.Identifier;

import java.util.Map;

public class JelloClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(JelloScreenHandlerTypes.COLOR_MIXER_TYPE, ColorMixerScreen::new);

        JelloClient.blockInit();

        JelloClient.itemInit();

        JelloClient.clientEventRegistry();

        JelloClient.registerCustomModelPredicate();
    }

    //------------------------------------------------------------------------------

    private static void blockInit() {
//        BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block)->{
//            ColorProviderRegistry.BLOCK.register((BlockColorProvider)block, block);
//            ColorProviderRegistry.ITEM.register((ItemColorProvider)block.asItem(), block.asItem());
//
//            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
//        });

        BlockRenderLayerMap.INSTANCE.putBlock(JelloBlocks.SLIME_SLAB, RenderLayer.getTranslucent());

//        BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block)->{
//            ColorProviderRegistry.BLOCK.register((BlockColorProvider)block, block);
//            ColorProviderRegistry.ITEM.register((ItemColorProvider)block.asItem(), block.asItem());
//
//            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
//        });

        for (Map.Entry<DyeColorant, DyedVariantContainer> dyedVariantEntry : DyedVariantContainer.getVariantMap().entrySet()) {
            //if (!Objects.equals(dyedVariantEntry.getKey().getId().getNamespace(), "minecraft")) {
            for (Block block : dyedVariantEntry.getValue().dyedBlocks.values()) {
                if (block instanceof SlimeBlockColored || block instanceof SlimeSlabColored) {
                    ColorProviderRegistry.BLOCK.register((BlockColorProvider) block, block);
                    ColorProviderRegistry.ITEM.register((ItemColorProvider) block.asItem(), block.asItem());

                    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
                }
            }
            //}
        }
    }

    private static void itemInit() {
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
}
