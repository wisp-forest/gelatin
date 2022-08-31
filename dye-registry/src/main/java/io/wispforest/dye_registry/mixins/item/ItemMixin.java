package io.wispforest.dye_registry.mixins.item;

import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;
import io.wispforest.dye_registry.ducks.DyeItemStorage;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements DyeItemStorage {

    private DyeColorant itemDyeColor = DyeColorantRegistry.NULL_VALUE_NEW;

    @Override
    public DyeColorant getDyeColorant() {
        return itemDyeColor;
    }

    @Override
    public void setDyeColor(DyeColorant dyeColorant) {
        this.itemDyeColor = dyeColorant;
    }
}
