package io.wispforest.jello.client;

import io.wispforest.common.CommonInit;
import io.wispforest.common.events.HotbarMouseEvents;
import io.wispforest.dye_entries.utils.DyeableVariantRegistry;
import io.wispforest.dye_entries.variants.DyeableVariantManager;
import io.wispforest.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.jello.Jello;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.client.render.DyeBundleTooltipRender;
import io.wispforest.jello.client.render.screen.ColorMixerScreen;
import io.wispforest.jello.client.render.screen.JelloScreenHandlerTypes;
import io.wispforest.jello.misc.JelloBlockVariants;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.SpongeItem;
import io.wispforest.jello.item.dyebundle.DyeBundleScreenEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BundleItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JelloClient implements ClientModInitializer {

    private static final RenderLayer TRANSLUCENT = RenderLayer.getTranslucent();

    @Override
    public void onInitializeClient() {

        DyeableVariantRegistry.registerModidModelRedirect(Jello.MODID);

        //-------------------------------[Other Block Stuff's]------------------------------

        ScreenRegistry.register(JelloScreenHandlerTypes.COLOR_MIXER_TYPE, ColorMixerScreen::new);

        //----------------------------------------------------------------------------------

        clientEventRegistry();

        //--------------------------------[Other Item Stuff's]------------------------------

        JelloItems.Slimeballs.SLIME_BALLS.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider) item, item));

        JelloItems.JelloCups.JELLO_CUP.forEach((item) -> ColorProviderRegistry.ITEM.register((ItemColorProvider) item, item));

        ColorProviderRegistry.ITEM.register((ItemColorProvider) JelloItems.ARTIST_PALETTE, JelloItems.ARTIST_PALETTE);

        FabricModelPredicateProviderRegistry.register(JelloItems.SPONGE, new Identifier("dirtiness"), (stack, world, entity, seed) -> SpongeItem.getDirtinessStage(stack));

        FabricModelPredicateProviderRegistry.register(JelloItems.DYE_BUNDLE, new Identifier("filled"), (stack, world, entity, seed) -> BundleItem.getAmountFilled(stack));

        //----------------------------------------------------------------------------------

        BlockRenderLayerMap.INSTANCE.putBlock(JelloBlocks.SLIME_SLAB,  TRANSLUCENT);

        for(Map.Entry<DyeColorant, DyeableVariantManager.DyeColorantVariantData> entrty : DyeableVariantManager.getVariantMap().entrySet()){
            for(Map.Entry<DyeableBlockVariant, Block> variantEntry : entrty.getValue().dyedBlocks().entrySet()){
                if(variantEntry.getKey() == JelloBlockVariants.SLIME_BLOCK || variantEntry.getKey() == JelloBlockVariants.SLIME_SLAB){
                    BlockRenderLayerMap.INSTANCE.putBlock(variantEntry.getValue(),  TRANSLUCENT);
                }
            }
        }
    }

    //------------------------------------------------------------------------------

    private static void clientEventRegistry() {
        HudRenderCallback.EVENT.register(new DyeBundleTooltipRender());

        HotbarMouseEvents.ALLOW_MOUSE_SCROLL.register(
                (player, horizontalAmount, verticalAmount) -> new DyeBundleScreenEvent().allowMouseScroll(player, horizontalAmount, verticalAmount));

        ClientLoginNetworking.registerGlobalReceiver(Jello.id("json_color_sync"), (client, handler, buf, listenerAdder) -> {
            PacketByteBuf buffer = PacketByteBufs.create();

            buffer.writeBoolean(CommonInit.getConfig().addCustomJsonColors);

            return CompletableFuture.completedFuture(buffer);
        });
    }

    //-------------------------------------------------------------------------------------

}
