package io.wispforest.jello.misc.behavior;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.ducks.DyeTool;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.ColorManipulators;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import io.wispforest.jello.api.events.CauldronEvent;
import io.wispforest.jello.api.util.ColorUtil;
import io.wispforest.jello.blockentity.ColorStorageBlockEntity;
import io.wispforest.jello.item.SpongeItem;
import io.wispforest.jello.item.dyebundle.DyeBundleItem;
import io.wispforest.jello.misc.JelloStats;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class JelloCauldronBehaviors implements CauldronBehavior {

    public static void registerJelloBehaviorBypass() {
        CauldronEvent.BEFORE_CAULDRON_BEHAVIOR.register(JelloCauldronBehaviors::changeColor);
        CauldronEvent.BEFORE_CAULDRON_BEHAVIOR.register(JelloCauldronBehaviors::cleanSponge);
        CauldronEvent.BEFORE_CAULDRON_BEHAVIOR.register(JelloCauldronBehaviors::cleanOrDyeBlock);
        CauldronEvent.BEFORE_CAULDRON_BEHAVIOR.register(JelloCauldronBehaviors::cleanOrDyeDyeableItem);
    }

    public static ActionResult changeColor(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CauldronEvent.CauldronType cauldronType) {
        if (cauldronType == CauldronEvent.CauldronType.WATER) {
            boolean isStack = false;

            if(stack.getItem() instanceof DyeTool dyeTool) {
                DyeColorant dyeColorant = dyeTool.attemptToDyeCauldron(world, player, pos, stack, hand);

                if (dyeColorant != DyeColorantRegistry.NULL_VALUE_NEW) {
                    ColorStorageBlockEntity blockEntity = (ColorStorageBlockEntity) world.getBlockEntity(pos);

                    if (blockEntity != null) {
                        if (dyeColorant == blockEntity.getDyeColorant()) {
                            return ActionResult.PASS;
                        }

                        if (!world.isClient) {
                            if (!player.getAbilities().creativeMode) {
                                if (isStack) {
                                    DyeBundleItem.dyeBundleInteraction(stack, dyeColorant);
                                } else {
                                    stack.decrement(1);
                                }
                            }

                            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));

                            blockEntity.setDyeColorant(dyeColorant);

                            world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            world.emitGameEvent((Entity) null, GameEvent.FLUID_PLACE, pos);
                        }

                        blockEntity.markDirty();

                        return ActionResult.success(world.isClient);
                    }
                }
            }
        }

        return ActionResult.PASS;
    }

    public static ActionResult cleanSponge(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CauldronEvent.CauldronType cauldronType) {
        if (cauldronType == CauldronEvent.CauldronType.WATER) {
            ColorStorageBlockEntity blockEntity = (ColorStorageBlockEntity) world.getBlockEntity(pos);

            if (blockEntity != null && ColorStorageBlockEntity.isWaterColored(blockEntity)) {
                return ActionResult.PASS;
            }

            if (stack.getItem() instanceof SpongeItem) {
                if (stack.getOrCreateNbt().getInt(SpongeItem.DIRTINESS_KEY) != 0) {
                    if (!world.isClient) {
                        stack.getOrCreateNbt().putInt(SpongeItem.DIRTINESS_KEY, 0);

                        LeveledCauldronBlock.decrementFluidLevel(state, world, pos);

                        world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.4F);
                    }
                    return ActionResult.success(world.isClient);
                }
            }
        }

        return ActionResult.PASS;
    }

    public static ActionResult cleanOrDyeBlock(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CauldronEvent.CauldronType cauldronType) {
        if (cauldronType == CauldronEvent.CauldronType.WATER) {
            Block block = Block.getBlockFromItem(stack.getItem());

            if (block == Blocks.AIR) {
                return ActionResult.PASS;
            }

            Block changedBlock;

            ColorStorageBlockEntity blockEntity = (ColorStorageBlockEntity) world.getBlockEntity(pos);

            if (blockEntity != null && ColorStorageBlockEntity.isWaterColored(blockEntity)) {
                changedBlock = DyeableBlockVariant.attemptToGetColoredBlock(block, blockEntity.getDyeColorant());
            } else {
                changedBlock = DyeableBlockVariant.attemptToGetColoredBlock(block, DyeColorantRegistry.NULL_VALUE_NEW);
            }

            if (changedBlock == null) {
                return ActionResult.PASS;
            }

            if (!ColorManipulators.changeBlockItemColor(world, pos, stack, changedBlock, player, hand, true)) {
                return player.shouldCancelInteraction() ? ActionResult.FAIL : ActionResult.CONSUME_PARTIAL;
            }

            if (!world.isClient) {
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);

                world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.4F);
            }

            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }

    public static ActionResult cleanOrDyeDyeableItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CauldronEvent.CauldronType cauldronType) {
        if (cauldronType == CauldronEvent.CauldronType.WATER) {
            Item item = stack.getItem();

            ColorStorageBlockEntity blockEntity = (ColorStorageBlockEntity) world.getBlockEntity(pos);

            if (item instanceof DyeableItem dyeableItem && blockEntity != null && ColorStorageBlockEntity.isWaterColored(blockEntity)) {
                if (!world.isClient) {
                    if (!dyeableItem.hasColor(stack)) {
                        float[] colorComp = blockEntity.getDyeColorant().getColorComponents();

                        int color = (int) (colorComp[0] * 255) << 16 | (int) (colorComp[1] * 255) << 8 | (int) (colorComp[2] * 255);

                        dyeableItem.setColor(stack, color);
                    } else {
                        player.setStackInHand(hand, ColorUtil.blendItemColorAndDyeColor(stack, List.of(blockEntity.getDyeColorant())));
                    }

                    LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                    player.incrementStat(JelloStats.DYE_ARMOR);

                    world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.4F);
                }

                return ActionResult.success(world.isClient);
            } else {
                return CLEAN_DYEABLE_ITEM.interact(state, world, pos, player, hand, stack);
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return null;
    }
}
