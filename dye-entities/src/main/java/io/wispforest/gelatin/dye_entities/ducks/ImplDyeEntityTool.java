package io.wispforest.gelatin.dye_entities.ducks;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entities.misc.EntityColorImplementations;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeStorage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface ImplDyeEntityTool extends DyeEntityTool, DyeStorage {

    default ActionResult attemptToDyeEntity(World world, PlayerEntity user, LivingEntity entity, ItemStack stack, Hand hand){
        if (user.getState(GelatinConstants.DYE_TOGGLE_SYNC_ID) && entity instanceof Colorable colorable && EntityColorImplementations.dyeEntityEvent(colorable, this.getDyeColorant())) {
            afterInteraction(user, hand, this.getDyeColorant());

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    default ActionResult attemptToDyeEntityCollar(World world, PlayerEntity player, Hand hand, CollarColorable storage) {
        ItemStack stack = player.getStackInHand(hand);

        if(!(stack.getItem() instanceof ImplDyeEntityTool dyeEntityTool)) return ActionResult.PASS;

        boolean bl = !DyeColorantRegistry.Constants.VANILLA_DYES.contains(dyeEntityTool.getDyeColorant())
                && storage.getCustomCollarColor() != dyeEntityTool.getDyeColorant()
                && storage.setCustomCollarColor(dyeEntityTool.getDyeColorant());

        if(bl) {
            dyeEntityTool.afterInteraction(player, hand, dyeEntityTool.getDyeColorant());

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
