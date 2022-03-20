package io.wispforest.jello.api.mixin.mixins.dye;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.mixin.ducks.DyeBlockEntityStorage;
import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin implements DyeBlockEntityStorage {

    @Unique private DyeColorant cachedDyeColorant = null;

    //Disallowing anyone to change the color of a shulker box as it is a static value for now
    @Override
    public void setDyeColor(DyeColorant dyeColorant) {}

    @Override
    public DyeColorant getDyeColor() {
        if (cachedDyeColorant == null){
            cachedDyeColorant = ((DyeBlockStorage) ((ShulkerBoxBlockEntity) (Object) this).getCachedState().getBlock()).getDyeColor();
        }

        return this.cachedDyeColorant;
    }
}
