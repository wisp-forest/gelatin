package io.wispforest.jello.api.mixin.mixins.accessors;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.WeakHashMap;

@Mixin(FabricItemInternals.class)
public interface FabricItemInternalsAccessor {
    @Accessor
    static WeakHashMap<Item.Settings, FabricItemInternals.ExtraData> getExtraData() {
        throw new UnsupportedOperationException();
    }

    @Mixin(FabricItemInternals.ExtraData.class)
    interface ExtraDataAccessor {
        @Accessor
        EquipmentSlotProvider getEquipmentSlotProvider();

        @Accessor
        CustomDamageHandler getCustomDamageHandler();
    }
}


