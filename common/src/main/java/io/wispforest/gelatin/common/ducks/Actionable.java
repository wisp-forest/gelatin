package io.wispforest.gelatin.common.ducks;

import net.minecraft.util.Identifier;

import java.util.Optional;

public interface Actionable {

    default boolean getState(Identifier actionID){
        return getStateOptional(actionID).orElse(false);
    }

    Optional<Boolean> getStateOptional(Identifier actionID);
}
