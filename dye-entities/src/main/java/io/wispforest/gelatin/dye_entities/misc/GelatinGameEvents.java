package io.wispforest.gelatin.dye_entities.misc;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GelatinGameEvents implements AutoRegistryContainer<GameEvent> {

    public static final GameEvent DYE_ENTITY = new GameEvent("dye_entity", 16);

    @Override
    public Registry<GameEvent> getRegistry() {
        return Registries.GAME_EVENT;
    }

    @Override
    public Class<GameEvent> getTargetFieldType() {
        return GameEvent.class;
    }
}
