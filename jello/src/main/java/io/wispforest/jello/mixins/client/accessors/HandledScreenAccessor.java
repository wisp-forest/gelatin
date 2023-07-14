package io.wispforest.jello.mixins.client.accessors;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("focusedSlot") Slot jello$getFocusedSlot();
    @Invoker("getSlotAt") Slot jello$getSlotAt(double x, double y);

    @Accessor("x") int jello$getX();
    @Accessor("y") int jello$getY();
}
