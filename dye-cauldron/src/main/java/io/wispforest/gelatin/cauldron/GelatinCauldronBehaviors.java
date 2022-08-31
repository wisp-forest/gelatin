package io.wispforest.gelatin.cauldron;

import io.wispforest.gelatin.cauldron.blockentity.ColorStorageBlockEntity;
import io.wispforest.gelatin.common.events.CauldronEvent;
import io.wispforest.gelatin.dye_entries.BlockColorManipulators;
import io.wispforest.gelatin.dye_entries.misc.GelatinStats;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockTool;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class GelatinCauldronBehaviors implements CauldronBehavior {

    public static void registerJelloBehaviorBypass() {
        CauldronEvent.BEFORE_CAULDRON_BEHAVIOR.register(GelatinCauldronBehaviors::changeColor);
        CauldronEvent.BEFORE_CAULDRON_BEHAVIOR.register(GelatinCauldronBehaviors::cleanOrDyeBlockItemOrItem);
        CauldronEvent.BEFORE_CAULDRON_BEHAVIOR.register(GelatinCauldronBehaviors::cleanOrDyeDyeableItem);
    }

    public static ActionResult changeColor(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CauldronEvent.CauldronType cauldronType) {
        if (cauldronType == CauldronEvent.CauldronType.WATER) {
            if(stack.getItem() instanceof DyeBlockTool dyeTool) {
                DyeColorant dyeColorant = dyeTool.attemptToDyeCauldron(world, player, pos, stack, hand);

                if (dyeColorant != DyeColorantRegistry.NULL_VALUE_NEW) {
                    ColorStorageBlockEntity blockEntity = (ColorStorageBlockEntity) world.getBlockEntity(pos);

                    if (blockEntity != null) {
                        if (dyeColorant == blockEntity.getDyeColorant()) {
                            return ActionResult.PASS;
                        }

                        if (!world.isClient) {
                            dyeTool.afterInteraction(player, hand, dyeColorant);

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

    public static ActionResult cleanOrDyeBlockItemOrItem(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CauldronEvent.CauldronType cauldronType) {
        if (cauldronType == CauldronEvent.CauldronType.WATER) {
            ItemConvertible changedEntry;

            if(stack.getItem() instanceof BlockItem blockItem){
                changedEntry = blockItem.getBlock();
            } else {
                changedEntry = stack.getItem();
            }

            if (changedEntry.asItem() == Items.AIR) {
                return ActionResult.PASS;
            }

            ItemConvertible changedBlock;
            ColorStorageBlockEntity blockEntity = (ColorStorageBlockEntity) world.getBlockEntity(pos);

            if (blockEntity != null && ColorStorageBlockEntity.isWaterColored(blockEntity)) {
                changedBlock = DyeableBlockVariant.attemptToGetColoredEntry(changedEntry, blockEntity.getDyeColorant());
            } else {
                changedBlock = DyeableBlockVariant.attemptToGetColoredEntry(changedEntry, DyeColorantRegistry.NULL_VALUE_NEW);
            }

            if (changedBlock == null) {
                return ActionResult.PASS;
            }

            if (!BlockColorManipulators.changeBlockItemOrItemColor(world, pos, stack, changedBlock, player, hand, true)) {
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
                        player.setStackInHand(hand, DyeColorant.blendItemColorAndDyeColor(stack, List.of(blockEntity.getDyeColorant())));
                    }

                    LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                    player.incrementStat(GelatinStats.DYE_ARMOR);

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
    public ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) { return ActionResult.PASS; }
}
