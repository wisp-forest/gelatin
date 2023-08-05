package io.wispforest.gelatin.common.pas;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerActionStatesStorage {

    void modifyStateEvent(Identifier syncID, Consumer<ActionState> consumer);

    //-----

    default Optional<Boolean> getState(PlayerEntity player, Identifier id){
        return getState(player.getUuid(), id);
    }

    default Optional<Boolean> getState(UUID pUUID, Identifier id){
        if(dosePlayerHaveStorage(pUUID)){
            Map<Identifier, ActionState> stateData = getPlayersStateStorage(pUUID);

            if(stateData.containsKey(id)) return Optional.of(stateData.get(id).get());
        }

        return Optional.empty();
    }

    Map<Identifier, ActionState> getPlayersStateStorage(UUID pUUID);

    boolean dosePlayerHaveStorage(UUID pUUID);
}
