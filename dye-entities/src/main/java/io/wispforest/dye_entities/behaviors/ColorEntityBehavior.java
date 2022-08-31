package io.wispforest.dye_entities.behaviors;

import io.wispforest.dye_entities.ducks.DyeableEntity;
import io.wispforest.dye_entities.ducks.ImplDyeEntityTool;
import io.wispforest.dye_entities.misc.GelatinGameEvents;
import io.wispforest.dye_registry.ducks.DyeEntityTool;
import io.wispforest.dye_registry.ducks.DyeItemStorage;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

//TODO find better way of doing this
public class ColorEntityBehavior extends FallibleItemDispenserBehavior {

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        World world = pointer.getWorld();
        if (!world.isClient()) {
            if(stack.getItem() instanceof ImplDyeEntityTool dyeEntityTool) {
                BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                this.setSuccess(tryColorEntity((ServerWorld) world, blockPos, dyeEntityTool));
                if (this.isSuccess()) {
                    dyeEntityTool.afterInteraction(stack, dyeEntityTool.getDyeColorant());
                    stack.decrement(1);
                }
            }

        }

        return stack;
    }

    private static boolean tryColorEntity(ServerWorld world, BlockPos pos, ImplDyeEntityTool dyeEntityTool) {
        for (LivingEntity livingEntity : world.getEntitiesByClass(LivingEntity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR)) {
            if (livingEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.getDyeColor() != dyeEntityTool.getDyeColorant()) {

                dyeableEntity.setDyeColor(dyeEntityTool.getDyeColorant());

                livingEntity.world.playSoundFromEntity((PlayerEntity) null, livingEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                world.emitGameEvent((Entity) null, GelatinGameEvents.DYE_ENTITY, pos);

                return true;
            }
        }

        return false;
    }
}
