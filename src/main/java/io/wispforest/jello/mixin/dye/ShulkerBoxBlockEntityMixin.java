package io.wispforest.jello.mixin.dye;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.ducks.DyeBlockEntityStorage;
import io.wispforest.jello.api.ducks.DyeBlockStorage;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin implements DyeBlockEntityStorage {

    @Unique private DyeColorant cachedDyeColorant = null;

    //Disallowing anyone to change the color of a shulker box as it is a static value for now
    @Override
    public void setDyeColor(DyeColorant dyeColorant) {}

    @Override
    public DyeColorant getDyeColor() {
        if (cachedDyeColorant == null) {
            cachedDyeColorant = ((DyeBlockStorage) ((ShulkerBoxBlockEntity) (Object) this).getCachedState().getBlock()).getDyeColor();
        }

        return this.cachedDyeColorant;
    }
}
