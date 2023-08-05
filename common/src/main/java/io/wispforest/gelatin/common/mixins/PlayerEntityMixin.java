package io.wispforest.gelatin.common.mixins;

import io.wispforest.gelatin.common.ducks.Actionable;
import io.wispforest.gelatin.common.pas.impl.ServerPlayerActionStates;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements Actionable {

    @Override
    public Optional<Boolean> getStateOptional(Identifier actionID) {
        return ServerPlayerActionStates.INSTANCE.getState(((PlayerEntity)(Object)this), actionID);
    }
}
