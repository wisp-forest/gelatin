package io.wispforest.gelatin.dye_entries.ducks;

import io.wispforest.gelatin.dye_entries.BlockColorManipulators;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockTool;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
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
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * This is the main interface for Implementing Jello DyeColorant System on to Minecrafts {@link DyeItem}
 */
@ApiStatus.Internal
public interface ImplDyeItemBlockTool extends DyeBlockTool, DyeItemStorage {

    default ActionResult attemptToDyeBlock(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand){
        if(player.shouldCancelInteraction() && this.getDyeColorant() != DyeColorantRegistry.NULL_VALUE_NEW) {
            BlockState blockState = world.getBlockState(blockPos);

            if (!BlockColorManipulators.changeBlockColor(world, blockPos, this.getDyeColorant(), player, true)) {
                return ActionResult.FAIL;
            }

            world.playSound(player, blockPos, blockState.getBlock().getSoundGroup(blockState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!world.isClient) {
                Random random = new Random();
                if (random.nextInt(10) == 0) {
                    afterInteraction(player, hand, this.getDyeColorant());
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    @NotNull
    default DyeColorant attemptToDyeCauldron(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand) {
        return this.getDyeColorant();
    }
}
