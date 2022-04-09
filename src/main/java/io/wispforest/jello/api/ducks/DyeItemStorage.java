package io.wispforest.jello.api.ducks;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.events.ColorBlockEventMethods;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface DyeItemStorage {

    default DyeColorant getDyeColorant() {
        return DyeColorantRegistry.NULL_VALUE_NEW;
    }

    default void setDyeColor(DyeColorant dyeColorant) {}

    //-------------------------------------------------------------------------------

    /**
     * Main method call for Dyeing any block before the items {@link Item#useOnBlock(ItemUsageContext)} method call. Override this for custom DyeColoring for an Item
     *
     * @param context Same usage Context as {@link Item#useOnBlock(ItemUsageContext)}
     * @return {@link ActionResult#PASS} if the event won't cancel or return any other result for the {@link Item#useOnBlock(ItemUsageContext)} to be stopped
     */
    default ActionResult attemptToDyeBlock(ItemUsageContext context){
        if (Jello.getConfig().enableDyeingBlocks && context.getPlayer() != null) {
            return this.attemptToDyeBlock(context.getWorld(), context.getBlockPos(), context.getPlayer(), context.getStack(), context.getHand());
        }

        return ActionResult.PASS;
    }

    /**
     * Main method call for Dyeing any block before the items {@link Item#useOnBlock(ItemUsageContext)} method call. Override this for custom DyeColoring for an Item
     *
     * @return {@link ActionResult#PASS} if the event won't cancel or return any other result for the {@link Item#useOnBlock(ItemUsageContext)} to be stopped
     */
    default ActionResult attemptToDyeBlock(World world, BlockPos blockPos, PlayerEntity player, ItemStack stack, Hand hand){
        if(player.shouldCancelInteraction() && this.getDyeColorant() != DyeColorantRegistry.NULL_VALUE_NEW) {
            BlockState blockState = world.getBlockState(blockPos);

            if (!ColorBlockEventMethods.changeBlockColor(world, blockPos, blockState, DyeableBlockVariant.attemptToGetColoredBlock(blockState.getBlock(), this.getDyeColorant()), player)) {
                return ActionResult.FAIL;
            }

            world.playSound(player, blockPos, blockState.getBlock().getSoundGroup(blockState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!world.isClient) {
                Random random = new Random();
                if (random.nextInt(10) == 0) {
                    ColorBlockEventMethods.decrementPlayerHandItemCC(player, hand);
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
