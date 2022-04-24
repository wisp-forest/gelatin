package io.wispforest.jello.misc;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class JelloGameEvents implements AutoRegistryContainer<GameEvent> {

    public static final GameEvent DYE_ENTITY = new GameEvent("dye_entity", 16);

    @Override
    public Registry<GameEvent> getRegistry() {
        return Registry.GAME_EVENT;
    }

    @Override
    public Class<GameEvent> getTargetFieldType() {
        return GameEvent.class;
    }
}
