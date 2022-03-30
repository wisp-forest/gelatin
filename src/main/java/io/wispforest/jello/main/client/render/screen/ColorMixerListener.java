package io.wispforest.jello.main.client.render.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;

@Environment(EnvType.CLIENT)

public class ColorMixerListener implements ScreenHandlerListener {

    private final MinecraftClient client;

    public ColorMixerListener(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {

    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

    }
}
