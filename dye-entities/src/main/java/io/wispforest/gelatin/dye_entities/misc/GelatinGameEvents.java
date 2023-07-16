package io.wispforest.gelatin.dye_entities.misc;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GelatinGameEvents {

    public static final GameEvent DYE_ENTITY = new GameEvent("dye_entity", 16);

    public static void init() {
        Registry.register(Registry.GAME_EVENT, GelatinConstants.id("dye_entity"), DYE_ENTITY);
    }
}
