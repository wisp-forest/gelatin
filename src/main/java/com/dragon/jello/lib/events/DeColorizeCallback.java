package com.dragon.jello.lib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface DeColorizeCallback {
    Event<DeColorizeCallback> EVENT = EventFactory.createArrayBacked(DeColorizeCallback.class,
            (listeners) -> (stack, world, user) -> {
                for (DeColorizeCallback event : listeners) {
                    if(!event.finishUsing(stack, world, user)){
                        return false;
                    }
                }

                return true;
            }
    );

    boolean finishUsing(ItemStack stack, World world, LivingEntity user);
}
