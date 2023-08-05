package io.wispforest.gelatin.common.mixins.client;

import io.wispforest.gelatin.common.pas.impl.client.ClientPlayerActionStates;
import io.wispforest.gelatin.common.ducks.Actionable;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin implements Actionable {

    @Override
    public Optional<Boolean> getStateOptional(Identifier actionID) {
        return ClientPlayerActionStates.INSTANCE.getState(((AbstractClientPlayerEntity)(Object)this), actionID);
    }
}
