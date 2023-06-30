package io.wispforest.jello.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class OnItemstackTooltipRenderEvent {

    public static Event<PreTooltipRender> PRE_TOOLTIP_RENDER = EventFactory.createArrayBacked(PreTooltipRender.class, preTooltipRenders ->
        (screen, matrices, stack, x, y) -> {
            boolean shouldRender = true;

            for(PreTooltipRender instance : preTooltipRenders){
                if(!instance.onRender(screen, matrices, stack, x, y)){
                    shouldRender = false;
                }
            }

            return shouldRender;
        }
    );


    public interface PreTooltipRender {
        boolean onRender(Screen screen, MatrixStack matrices, ItemStack stack, int x, int y);
    }
}
