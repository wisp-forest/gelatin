package com.dragon.jello.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
