package io.wispforest.jello.mixins.client.accessors;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net/minecraft/client/gui/screen/ingame/CreativeInventoryScreen$CreativeSlot")
public interface CreativeSlotAccessor {
    @Accessor("slot") Slot jello$getSlot();
}
