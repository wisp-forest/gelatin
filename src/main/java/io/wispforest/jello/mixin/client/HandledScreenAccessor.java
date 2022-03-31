package io.wispforest.jello.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {

    @Invoker("getSlotAt")
    Slot jello$getSlotAt(double x, double y);

}
