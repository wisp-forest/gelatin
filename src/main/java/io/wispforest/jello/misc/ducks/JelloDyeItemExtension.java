package io.wispforest.jello.misc.ducks;

import io.wispforest.jello.api.ducks.DyeItemStorage;
import io.wispforest.jello.api.ducks.DyeTool;
import io.wispforest.jello.api.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.ColorManipulators;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.item.JelloDyeItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Random;

/**
 * This is the main interface for Implementing Jello DyeColorant System on to Minecrafts {@link DyeItem} and Jellos Enhanced {@link JelloDyeItem}
 */
@ApiStatus.Internal
public interface JelloDyeItemExtension extends DyeTool, DyeItemStorage {

    @Override
    default boolean isDyeItem() {
        return true;
    }

    default ActionResult attemptToDyeBlock(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand){
        if(player.shouldCancelInteraction() && this.getDyeColorant() != DyeColorantRegistry.NULL_VALUE_NEW) {
            BlockState blockState = world.getBlockState(blockPos);

            if (!ColorManipulators.changeBlockColor(world, blockPos, this.getDyeColorant(), player, true)) {
                return ActionResult.FAIL;
            }

            world.playSound(player, blockPos, blockState.getBlock().getSoundGroup(blockState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!world.isClient) {
                Random random = new Random();
                if (random.nextInt(10) == 0) {
                    ColorManipulators.decrementPlayerHandItemCC(player, hand);
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    default ActionResult attemptToDyeEntity(World world, PlayerEntity user, DyeableEntity entity, ItemStack stack, Hand hand){
        if (user.shouldCancelInteraction()) {
            if(ColorManipulators.dyeEntityEvent(entity, this.getDyeColorant())){
                ColorManipulators.decrementPlayerHandItemCC(user, hand);

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    default DyeColorant attemptToDyeCauldron(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand) {
        return this.getDyeColorant();
    }
}
