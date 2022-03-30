package io.wispforest.jello.main.client;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.api.events.HotbarMouseEvents;
import io.wispforest.jello.main.client.render.DyeBundleTooltipRender;
import io.wispforest.jello.main.client.render.screen.ColorMixerScreen;
import io.wispforest.jello.main.client.render.screen.JelloScreenHandlerRegistry;
import io.wispforest.jello.main.common.blocks.JelloBlockRegistry;
import io.wispforest.jello.main.common.blocks.SlimeBlockColored;
import io.wispforest.jello.main.common.blocks.SlimeSlabColored;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.jello.main.common.items.SpongeItem;
import io.wispforest.jello.main.common.items.dyebundle.DyeBundleScreenEvent;
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
        ScreenRegistry.register(JelloScreenHandlerRegistry.COLOR_MIXER_TYPE, ColorMixerScreen::new);

        JelloClient.blockInit();

        JelloClient.itemInit();

        JelloClient.clientEventRegistry();

        JelloClient.registerCustomModelPredicate();
    }

    //------------------------------------------------------------------------------

    private static void blockInit(){
//        BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block)->{
//            ColorProviderRegistry.BLOCK.register((BlockColorProvider)block, block);
//            ColorProviderRegistry.ITEM.register((ItemColorProvider)block.asItem(), block.asItem());
//
//            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
//        });

        BlockRenderLayerMap.INSTANCE.putBlock(JelloBlockRegistry.SLIME_SLAB, RenderLayer.getTranslucent());

//        BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block)->{
//            ColorProviderRegistry.BLOCK.register((BlockColorProvider)block, block);
//            ColorProviderRegistry.ITEM.register((ItemColorProvider)block.asItem(), block.asItem());
//
//            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
//        });

        for(Map.Entry<DyeColorant, DyedVariantContainer> dyedVariantEntry : DyedVariantContainer.getVariantMap().entrySet()) {
            //if (!Objects.equals(dyedVariantEntry.getKey().getId().getNamespace(), "minecraft")) {
                for (Block block : dyedVariantEntry.getValue().dyedBlocks.values()) {
                    if (block instanceof SlimeBlockColored || block instanceof SlimeSlabColored) {
                        ColorProviderRegistry.BLOCK.register((BlockColorProvider)block, block);
                        ColorProviderRegistry.ITEM.register((ItemColorProvider)block.asItem(), block.asItem());

                        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
                    }
                }
            //}
        }
    }

    private static void itemInit(){
        ItemRegistry.SlimeBallItemRegistry.SLIME_BALLS.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider)item, item));

        ItemRegistry.JelloCupItemRegistry.JELLO_CUP.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider)item, item));

        ColorProviderRegistry.ITEM.register((ItemColorProvider)ItemRegistry.MainItemRegistry.ARTIST_PALETTE, ItemRegistry.MainItemRegistry.ARTIST_PALETTE);
    }

    //-------------------------------------------------------------------------------------

    private static void registerCustomModelPredicate(){
        FabricModelPredicateProviderRegistry.register(ItemRegistry.MainItemRegistry.SPONGE, new Identifier("dirtiness"), (stack, world, entity, seed) -> SpongeItem.getDirtinessStage(stack));

        FabricModelPredicateProviderRegistry.register(ItemRegistry.MainItemRegistry.DYE_BUNDLE, new Identifier("filled"), (stack, world, entity, seed) -> BundleItem.getAmountFilled(stack));
    }

    //-------------------------------------------------------------------------------------

    private static void clientEventRegistry(){
        HudRenderCallback.EVENT.register(new DyeBundleTooltipRender());

        HotbarMouseEvents.ALLOW_MOUSE_SCROLL.register(
                (player, horizontalAmount, verticalAmount) -> new DyeBundleScreenEvent().allowMouseScroll(player, horizontalAmount, verticalAmount));

    }
}
