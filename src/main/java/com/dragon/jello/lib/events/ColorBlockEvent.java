package com.dragon.jello.lib.events;

import com.dragon.jello.common.Jello;
import com.dragon.jello.common.blocks.BlockRegistry;
import com.dragon.jello.lib.registry.ColorBlockRegistry;
import com.dragon.jello.mixin.mixins.common.accessors.ShulkerBoxBlockEntityAccessor;
import com.dragon.jello.common.data.tags.JelloTags;
import io.wispforest.owo.ops.ItemOps;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Random;

public class ColorBlockEvent implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        Item mainHandItem = player.getMainHandStack().getItem();
        BlockState blockState = world.getBlockState(hitResult.getBlockPos());

        //Identifier id = Registry.BLOCK.getId(blockState.getBlock());

        if (player.shouldCancelInteraction()) {
            DyeItem dyeItem;
            SoundEvent soundEvent;

            if(mainHandItem instanceof DyeItem dyeItemInHand){
                dyeItem = dyeItemInHand;
                soundEvent = blockState.getBlock().getSoundGroup(blockState).getPlaceSound();
            } else if(mainHandItem == Blocks.WET_SPONGE.asItem()){
                dyeItem = null;
                soundEvent = SoundEvents.ITEM_BUCKET_EMPTY;
            } else{
                return ActionResult.PASS;
            }

            Block changedBlock = ColorBlockRegistry.getVariant(blockState.getBlock(), dyeItem == null ? null : dyeItem.getColor());

            if(changedBlock == null || changedBlock == blockState.getBlock()){
                return ActionResult.FAIL;
            }

            if (world.getBlockEntity(hitResult.getBlockPos()) instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
                if (shulkerBoxBlockEntity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
                    NbtCompound tag = new NbtCompound();
                    ((ShulkerBoxBlockEntityAccessor) shulkerBoxBlockEntity).callWriteNbt(tag);

                    if (!world.isClient) {
                        world.setBlockState(hitResult.getBlockPos(), changedBlock.getStateWithProperties(blockState));
                        world.getBlockEntity(hitResult.getBlockPos()).readNbt(tag);
                    }
                } else {
                    return ActionResult.FAIL;
                }
            }
            else if (world.getBlockEntity(hitResult.getBlockPos()) instanceof BedBlockEntity) {
                BlockPos pos = hitResult.getBlockPos();
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

            }
            else if (!world.isClient) {
                world.setBlockState(hitResult.getBlockPos(), changedBlock.getStateWithProperties(blockState));
            }


            if(soundEvent == SoundEvents.ITEM_BUCKET_EMPTY){
                player.playSound(soundEvent, 1.0F, 1.55F);
            }
            else{
                world.playSound(player, hitResult.getBlockPos(), soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);

                Random random = new Random();

                if(random.nextInt(10) == 0){
                    decrementPlayerHandItemCC(player, hand);
                }
            }

            return ActionResult.SUCCESS;

        }

        return ActionResult.PASS;
    }

    private void decrementPlayerHandItemCC(PlayerEntity player, Hand hand) {
        if (!player.getAbilities().creativeMode) {
            ItemOps.decrementPlayerHandItem(player, hand);
        }
    }
}
