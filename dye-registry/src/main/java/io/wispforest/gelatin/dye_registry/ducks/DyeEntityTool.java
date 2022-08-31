package io.wispforest.gelatin.dye_registry.ducks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface DyeEntityTool extends DyeBaseTool{

    /**
     * Main method call for Dyeing any Entity before the items {@link Item#useOnEntity(ItemStack, PlayerEntity, LivingEntity, Hand)} method call. Override this for custom DyeColoring for an Item
     *
     * @return {@link ActionResult#PASS} if the event won't cancel or return any other result for the {@link Item#useOnBlock(ItemUsageContext)} to be stopped
     */
    default ActionResult attemptToDyeEntity(World world, PlayerEntity player, LivingEntity entity, ItemStack stack, Hand hand){
        return ActionResult.PASS;
    }

    default ActionResult attemptToDyeEntityCollar(World world, PlayerEntity player, Hand hand, CustomCollarColorStorage collarAbleEntity){
        return ActionResult.PASS;
    }

}
