package com.dragon.jello.events;

import com.dragon.jello.Jello;
import com.dragon.jello.mixin.ducks.DyeableEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class DeColorEntityBehavior extends FallibleItemDispenserBehavior {

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        World world = pointer.getWorld();
        if (!world.isClient()) {
            if(stack.getItem() == Items.WATER_BUCKET){
                BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                this.setSuccess(tryColorEntity((ServerWorld)world, blockPos, stack));
                if (this.isSuccess()) {
                    return new ItemStack(Items.BUCKET);
                }
            }

        }

        return stack;
    }

    private static boolean tryColorEntity(ServerWorld world, BlockPos pos, ItemStack stack) {
        for(LivingEntity livingEntity : world.getEntitiesByClass(LivingEntity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR)) {
            if(new ColorEntityEvent().finishUsing(stack, world, livingEntity)){
                return true;
            }
        }

        return false;
    }
}
