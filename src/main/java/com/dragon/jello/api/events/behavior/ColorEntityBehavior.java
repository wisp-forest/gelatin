package com.dragon.jello.api.events.behavior;

import com.dragon.jello.main.common.Jello;
import com.dragon.jello.api.dye.DyeColorant;
import com.dragon.jello.api.mixin.ducks.DyeRedirect;
import com.dragon.jello.api.mixin.ducks.DyeableEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ColorEntityBehavior extends FallibleItemDispenserBehavior {

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        World world = pointer.getWorld();
        if (!world.isClient()) {
            if(stack.getItem() instanceof DyeItem dyeItem){
                BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                this.setSuccess(tryColorEntity((ServerWorld)world, blockPos, ((DyeRedirect)dyeItem).getDyeColor()));
                if (this.isSuccess()) {
                    stack.decrement(1);
                }
            }

        }

        return stack;
    }

    private static boolean tryColorEntity(ServerWorld world, BlockPos pos, DyeColorant dyeColor) {
        for(LivingEntity livingEntity : world.getEntitiesByClass(LivingEntity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR)) {
            if (livingEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.getDyeColor() != dyeColor) {
                dyeableEntity.setDyeColor(dyeColor);

                livingEntity.world.playSoundFromEntity((PlayerEntity)null, livingEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                world.emitGameEvent((Entity) null, Jello.GameEvents.DYE_ENTITY, pos);

                return true;
            }
        }

        return false;
    }
}
