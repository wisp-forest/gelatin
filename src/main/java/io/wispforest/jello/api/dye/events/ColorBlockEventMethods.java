package io.wispforest.jello.api.dye.events;

import io.wispforest.jello.api.JelloAPI;
import io.wispforest.jello.mixin.accessors.ShulkerBoxBlockEntityAccessor;
import io.wispforest.owo.ops.ItemOps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ColorBlockEventMethods {

    public static boolean changeBlockColor(World world, BlockPos blockPos, BlockState oldBlockState, Block changedBlock, PlayerEntity player) {
        if (changedBlock == null || changedBlock == oldBlockState.getBlock()) {
            return false;
        }

        if (world.getBlockEntity(blockPos) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
            if (shulkerBoxBlockEntity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                NbtCompound tag = new NbtCompound();
                ((ShulkerBoxBlockEntityAccessor) shulkerBoxBlockEntity).callWriteNbt(tag);

                if (!world.isClient) {
                    world.setBlockState(blockPos, changedBlock.getStateWithProperties(oldBlockState));
                    world.getBlockEntity(blockPos).readNbt(tag);
                }
            } else {
                return false;
            }
        } else if (world.getBlockEntity(blockPos) instanceof BedBlockEntity) {
            BlockPos pos = blockPos;
            BlockState bedPart = world.getBlockState(pos);

            Direction facingDirection = BedBlock.getDirection(world, pos);

            if (bedPart.get(BedBlock.PART) == BedPart.HEAD) {
                pos = pos.offset(facingDirection.getOpposite());

                bedPart = world.getBlockState(pos);
            }

            if (!world.isClient) {
                BlockState changedState = changedBlock.getDefaultState().with(HorizontalFacingBlock.FACING, bedPart.get(HorizontalFacingBlock.FACING));

                world.setBlockState(pos.offset(bedPart.get(HorizontalFacingBlock.FACING)), Blocks.AIR.getDefaultState());

                world.setBlockState(pos, changedState);
                changedState.getBlock().onPlaced(world, pos, changedState, player, ItemStack.EMPTY);
            }

        } else if (!world.isClient) {
            world.setBlockState(blockPos, changedBlock.getStateWithProperties(oldBlockState));
        }

        return true;
    }

    public static boolean changeBlockItemColor(World world, BlockPos cauldronPos, ItemStack oldBlockStack, Block changedBlock, PlayerEntity player, Hand hand, boolean washingBlock) {
        Block oldBlock = Block.getBlockFromItem(oldBlockStack.getItem());

        if (changedBlock == null || changedBlock == oldBlock) {
            return false;
        }

        if (oldBlock instanceof ShulkerBoxBlock) {
            if (!world.isClient) {
                ItemStack itemStack = new ItemStack(changedBlock);
                if (oldBlockStack.hasNbt()) {
                    itemStack.setNbt(oldBlockStack.getNbt().copy());
                }

                player.setStackInHand(hand, itemStack);

                if (washingBlock) {
                    player.incrementStat(Stats.CLEAN_SHULKER_BOX);
                } else {
                    player.incrementStat(JelloAPI.Stats.DYE_SHULKER_BOX);
                }
            }

            return true;
        } else if (!world.isClient) {
            int stackDecrementAmount = 1;

            if (oldBlock.asItem().getMaxCount() > 1) {
                stackDecrementAmount = Math.min(oldBlockStack.getCount(), 8);
            }

            ItemStack changedItemStack = new ItemStack(changedBlock, stackDecrementAmount);

            if (!player.getAbilities().creativeMode) {
                oldBlockStack.decrement(stackDecrementAmount);
            }

            BlockPos blockPos = cauldronPos.up();
            ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), changedItemStack);

            if (washingBlock) {
                player.incrementStat(JelloAPI.Stats.CLEAN_BLOCK);
            } else {
                player.incrementStat(JelloAPI.Stats.DYE_BLOCK);
            }
        }

        return true;
    }

    public static void decrementPlayerHandItemCC(PlayerEntity player, Hand hand) {
        if (!player.getAbilities().creativeMode) {
            ItemOps.decrementPlayerHandItem(player, hand);
        }
    }
}
