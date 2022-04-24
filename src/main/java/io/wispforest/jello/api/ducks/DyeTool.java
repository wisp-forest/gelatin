package io.wispforest.jello.api.ducks;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.misc.dye.JelloBlockVariants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface used to store Color Data within A Colorable Item.
 * <p>Look at {@link VanillaBlockVariants} and {@link JelloBlockVariants} as an example of some blocks by default</p>
 */
public interface DyeTool {
    /**
     * Main method call for Dyeing any block before the items {@link Item#useOnBlock(ItemUsageContext)} method call. Override this for custom DyeColoring for an Item
     *
     * @param context Same usage Context as {@link Item#useOnBlock(ItemUsageContext)}
     * @return {@link ActionResult#PASS} if the event won't cancel or return any other result for the {@link Item#useOnBlock(ItemUsageContext)} to be stopped
     */
    default ActionResult attemptToDyeBlock(ItemUsageContext context){
        if (Jello.getConfig().enableDyeingBlocks && context.getPlayer() != null) {
            return this.attemptToDyeBlock(context.getWorld(), context.getPlayer(), context.getBlockPos(), context.getStack(), context.getHand());
        }

        return ActionResult.PASS;
    }

    /**
     * Main method call for Dyeing any block before the items {@link Item#useOnBlock(ItemUsageContext)} method call. Override this for custom DyeColoring for an Item
     *
     * @return {@link ActionResult#PASS} if the event won't cancel or return any other result for the {@link Item#useOnBlock(ItemUsageContext)} to be stopped
     */
    default ActionResult attemptToDyeBlock(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand){
        return ActionResult.PASS;
    }

    /**
     * Main method call for Dyeing any Entity before the items {@link Item#useOnEntity(ItemStack, PlayerEntity, LivingEntity, Hand)} method call. Override this for custom DyeColoring for an Item
     *
     * @return {@link ActionResult#PASS} if the event won't cancel or return any other result for the {@link Item#useOnBlock(ItemUsageContext)} to be stopped
     */
    default ActionResult attemptToDyeEntity(World world, PlayerEntity player, DyeableEntity entity, ItemStack stack, Hand hand){
        return ActionResult.PASS;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------

    default DyeColorant attemptToDyeCauldron(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand){
        return null;
    }
}
