package io.wispforest.jello.mixin.dye.item;

import io.wispforest.jello.api.ducks.DyeItemStorage;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
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
