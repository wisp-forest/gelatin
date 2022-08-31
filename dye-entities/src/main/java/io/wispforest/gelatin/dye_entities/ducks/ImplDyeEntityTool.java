package io.wispforest.gelatin.dye_entities.ducks;

import io.wispforest.gelatin.dye_entities.misc.EntityColorManipulators;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.CustomCollarColorStorage;
import io.wispforest.gelatin.dye_registry.ducks.DyeEntityTool;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface ImplDyeEntityTool extends DyeEntityTool, DyeItemStorage {

    default ActionResult attemptToDyeEntity(World world, PlayerEntity user, LivingEntity entity, ItemStack stack, Hand hand){
        if (user.shouldCancelInteraction() && entity instanceof DyeableEntity dyeableEntity) {
            if(EntityColorManipulators.dyeEntityEvent(dyeableEntity, this.getDyeColorant())){
                afterInteraction(user, hand, this.getDyeColorant());

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    default ActionResult attemptToDyeEntityCollar(World world, PlayerEntity player, Hand hand, CustomCollarColorStorage collarAbleEntity) {
        ItemStack stack = player.getStackInHand(hand);

        if(stack.getItem() instanceof ImplDyeEntityTool dyeEntityTool && !DyeColorantRegistry.Constants.VANILLA_DYES.contains(dyeEntityTool.getDyeColorant())){
            if(collarAbleEntity.getCustomCollarColor() != dyeEntityTool.getDyeColorant() || collarAbleEntity.isRainbowCollared()) {
                if(!world.isClient) {
                    collarAbleEntity.setCustomCollarColor(dyeEntityTool.getDyeColorant());
                    collarAbleEntity.setRainbowCollar(false);
                }

                dyeEntityTool.afterInteraction(player, hand, dyeEntityTool.getDyeColorant());

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}
