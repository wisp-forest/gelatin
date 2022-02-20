package com.dragon.jello.lib.events;

import com.dragon.jello.common.Jello;
import com.dragon.jello.common.blocks.BlockRegistry;
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

        Identifier id = Registry.BLOCK.getId(blockState.getBlock());

        if (player.shouldCancelInteraction() && (id.getNamespace().equals("minecraft") || id.getNamespace().equals("jello"))) {
            if (mainHandItem instanceof DyeItem dyeItem) {
                if (getColorPrefix(id).equals(dyeItem.getColor().getName())) {
                    return ActionResult.FAIL;
                }

                String blockType = isVanillaColorBlock(blockState);

                if (blockType.equals("pass")) { return ActionResult.PASS; }

                Block changedBlock;

                if(blockType.equals("_slime_block")){
                    changedBlock = Registry.BLOCK.get(new Identifier(Jello.MODID,dyeItem.getColor().getName() + blockType));
                }else{
                    changedBlock = Registry.BLOCK.get(new Identifier(id.getNamespace(),dyeItem.getColor().getName() + blockType));
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
                } else if (world.getBlockEntity(hitResult.getBlockPos()) instanceof BedBlockEntity) {
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

                } else if (!world.isClient) {
                    world.setBlockState(hitResult.getBlockPos(), changedBlock.getStateWithProperties(blockState));
                }

                world.playSound(player, hitResult.getBlockPos(), blockState.getBlock().getSoundGroup(blockState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

                Random random = new Random();

                if(random.nextInt(10) == 0){
                    decrementPlayerHandItemCC(player, hand);
                }

                return ActionResult.SUCCESS;
            } else if (mainHandItem == Blocks.WET_SPONGE.asItem()) {
                return cleanBlockEvent(player, world, blockState, hitResult, player.getMainHandStack());
            }
        }

        return ActionResult.PASS;
    }

    private ActionResult cleanBlockEvent(PlayerEntity player, World world, BlockState blockState, BlockHitResult hitResult, ItemStack mainHandStack) {
        if (isAlreadyDefault(blockState.getBlock())) {
            return ActionResult.FAIL;
        }

        Identifier defaultBlockName;

        if (blockState.isIn(BlockTags.TERRACOTTA)) {
            defaultBlockName = new Identifier("terracotta");
        } else if (blockState.isIn(BlockTags.CANDLES)) {
            defaultBlockName = new Identifier("candle");
        } else if (blockState.isIn(BlockTags.IMPERMEABLE)) {
            defaultBlockName = new Identifier("glass");
        } else if (blockState.isIn(JelloTags.Blocks.GLASS_PANES)) {
            defaultBlockName = new Identifier("glass_pane");
        } else if (blockState.isIn(BlockTags.WOOL)) {
            defaultBlockName = new Identifier("white_wool");
        } else if (blockState.isIn(BlockTags.CARPETS)) {
            defaultBlockName = new Identifier("white_carpet");
        } else if (blockState.isIn(BlockTags.BEDS)) {
            defaultBlockName = new Identifier("white_bed");
        } else if (blockState.isIn(JelloTags.Blocks.CONCRETE)) {
            defaultBlockName = new Identifier("white_concrete");
        } else if (blockState.isIn(JelloTags.Blocks.CONCRETE_POWDER)) {
            defaultBlockName = new Identifier("white_concrete_powder");
        } else if (blockState.isIn(BlockTags.SHULKER_BOXES)) {
            defaultBlockName = new Identifier("shulker_box");
        } else if (blockState.isIn(JelloTags.Blocks.SLIME_BLOCKS)) {
            defaultBlockName = new Identifier("slime_block");
        } else if (blockState.isIn(JelloTags.Blocks.SLIME_SLABS)) {
            defaultBlockName = new Identifier(Jello.MODID,"slime_slab");
        } else {
            return ActionResult.PASS;
        }

        Block changedBlock = Registry.BLOCK.get(defaultBlockName);

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
        } else if (world.getBlockEntity(hitResult.getBlockPos()) instanceof BedBlockEntity) {
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

        } else if (!world.isClient) {
            world.setBlockState(hitResult.getBlockPos(), changedBlock.getStateWithProperties(blockState));
        }

        player.playSound(SoundEvents.ITEM_BUCKET_EMPTY, 1.0F, 1.55F);

        return ActionResult.SUCCESS;
    }

    private void decrementPlayerHandItemCC(PlayerEntity player, Hand hand) {
        if (!player.getAbilities().creativeMode) {
            ItemOps.decrementPlayerHandItem(player, hand);
        }
    }

    private String getColorPrefix(Identifier identifier) {
        String[] splitName = identifier.getPath().split("_");
        if (splitName.length >= 2) {
            return splitName[0].equals("light")  ? splitName[0] + "_" + splitName[1] : splitName[0];
        } else {
            return "";
        }
    }

    private boolean isAlreadyDefault(Block block) {
        if (block == Blocks.GLASS || block == Blocks.TINTED_GLASS) {
            return true;
        } else if (block == Blocks.TERRACOTTA) {
            return true;
        } else if (block == Blocks.CANDLE) {
            return true;
        } else if (block == Blocks.WHITE_WOOL || block == Blocks.WHITE_CARPET) {
            return true;
        } else if (block == Blocks.SHULKER_BOX) {
            return true;
        } else if (block == Blocks.WHITE_BED) {
            return true;
        } else if (block == Blocks.SLIME_BLOCK || block == BlockRegistry.SlimeSlabRegistry.SLIME_SLAB) {
            return true;
        } else {
            return false;
        }
    }

    private String isVanillaColorBlock(BlockState blockState) {
        if (blockState.isIn(BlockTags.WOOL)) {
            return "_wool";
        } else if (blockState.isIn(BlockTags.CARPETS)) {
            return "_carpet";
        } else if (blockState.isIn(BlockTags.BEDS)) {
            return "_bed";
        } else if (blockState.isIn(BlockTags.TERRACOTTA)) {
            return "_terracotta";
        } else if (blockState.isIn(JelloTags.Blocks.CONCRETE)) {
            return "_concrete";
        } else if (blockState.isIn(JelloTags.Blocks.CONCRETE_POWDER)) {
            return "_concrete_powder";
        } else if (blockState.isIn(BlockTags.CANDLES)) {
            return "_candle";
        } else if (blockState.isIn(BlockTags.CANDLE_CAKES)) {
            return "_candle_cake";
        } else if (blockState.isIn(BlockTags.SHULKER_BOXES)) {
            return "_shulker_box";
        } else if (blockState.isIn(BlockTags.IMPERMEABLE)) {
            return "_stained_glass";
        } else if (blockState.isIn(JelloTags.Blocks.GLASS_PANES)) {
            return "_stained_glass_pane";
        } else if (blockState.isIn(JelloTags.Blocks.SLIME_BLOCKS)) {
            return "_slime_block";
        } else if (blockState.isIn(JelloTags.Blocks.SLIME_SLABS)) {
            return "_slime_slab";
        } else {
            return "pass";
        }
    }
}
