package io.wispforest.jello.main.client;

import io.wispforest.jello.api.events.HotbarMouseEvents;
import io.wispforest.jello.main.client.render.DyeBundleTooltipRender;
import io.wispforest.jello.main.common.blocks.BlockRegistry;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.jello.main.common.items.SpongeItem;
import io.wispforest.jello.main.common.items.dyebundle.DyeBundleScreenEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BundleItem;
import net.minecraft.util.Identifier;

public class JelloClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        JelloClient.blockInit();

        JelloClient.itemInit();

        JelloClient.clientEventRegistry();

        JelloClient.registerCustomModelPredicate();
    }

    //------------------------------------------------------------------------------

    private static void blockInit(){
        BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block)->{
            ColorProviderRegistry.BLOCK.register((BlockColorProvider)block, block);
            ColorProviderRegistry.ITEM.register((ItemColorProvider)block.asItem(), block.asItem());

            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
        });

        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.SlimeSlabRegistry.SLIME_SLAB, RenderLayer.getTranslucent());

        BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block)->{
            ColorProviderRegistry.BLOCK.register((BlockColorProvider)block, block);
            ColorProviderRegistry.ITEM.register((ItemColorProvider)block.asItem(), block.asItem());

            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
        });
    }

    private static void itemInit(){
        ItemRegistry.SlimeBallItemRegistry.SLIME_BALLS.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider)item, item));

        ItemRegistry.JelloCupItemRegistry.JELLO_CUP.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider)item, item));

        ColorProviderRegistry.ITEM.register((ItemColorProvider)ItemRegistry.MainItemRegistry.DYNAMIC_DYE, ItemRegistry.MainItemRegistry.DYNAMIC_DYE);
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
