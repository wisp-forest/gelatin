package com.dragon.jello.common.blocks.cauldron;

import com.dragon.jello.common.blocks.BlockRegistry;
import com.dragon.jello.mixin.ducks.DyeableCauldron;
import com.dragon.jello.mixin.ducks.DyeableItemExt;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Map;

public interface JelloCauldronBehaviors extends CauldronBehavior {

    static void registerJelloBehavior() {
        for(DyeColor dyeColor : DyeColor.values()){
            Item dyeItem = Registry.ITEM.get(new Identifier(dyeColor.getName() + "_dye"));

            CauldronBehavior cauldronBehavior = (state, world, pos, player, hand, stack) -> changeColor(
                    world,
                    pos,
                    player,
                    hand,
                    stack,
                    Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, state.get(LeveledCauldronBlock.LEVEL)).with(DyeableCauldron.DYE_COLOR, ((DyeItem)dyeItem).getColor().getId()),
                    SoundEvents.ITEM_BUCKET_EMPTY
            );

            WATER_CAULDRON_BEHAVIOR.put(dyeItem, cauldronBehavior);
        }

    }

    static ActionResult changeColor(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent) {
        if (!world.isClient) {
            Item item = stack.getItem();
            if(!player.getAbilities().creativeMode){
                player.getMainHandStack().decrement(1);
            }
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            world.setBlockState(pos, state);
            world.playSound((PlayerEntity)null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
        }

        return ActionResult.success(world.isClient);
    }

    static ActionResult dyeItem(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent) {
        if (!world.isClient) {
            Item item = stack.getItem();
            if(!player.getAbilities().creativeMode){
                player.getMainHandStack().decrement(1);
            }
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            world.setBlockState(pos, state);
            world.playSound((PlayerEntity)null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
        }

        return ActionResult.success(world.isClient);
    }

    CauldronBehavior DYE_SHULKER_BOX = (state, world, pos, player, hand, stack) -> {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return ActionResult.PASS;
        } else {
            if (!world.isClient) {
                ItemStack itemStack = new ItemStack(Blocks.SHULKER_BOX);
                if (stack.hasNbt()) {
                    itemStack.setNbt(stack.getNbt().copy());
                }

                player.setStackInHand(hand, itemStack);
                player.incrementStat(Stats.CLEAN_SHULKER_BOX);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }

            return ActionResult.success(world.isClient);
        }
    };
    CauldronBehavior DYE_BANNER = (state, world, pos, player, hand, stack) -> {
        if (BannerBlockEntity.getPatternCount(stack) <= 0) {
            return ActionResult.PASS;
        } else {
            if (!world.isClient) {
                ItemStack itemStack = stack.copy();
                itemStack.setCount(1);
                BannerBlockEntity.loadFromItemStack(itemStack);
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                if (stack.isEmpty()) {
                    player.setStackInHand(hand, itemStack);
                } else if (player.getInventory().insertStack(itemStack)) {
                    player.playerScreenHandler.syncState();
                } else {
                    player.dropItem(itemStack, false);
                }

                player.incrementStat(Stats.CLEAN_BANNER);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }

            return ActionResult.success(world.isClient);
        }
    };
    CauldronBehavior DYE_DYEABLE_ITEM = (state, world, pos, player, hand, stack) -> {
        Item item = stack.getItem();
        if (!(item instanceof DyeableItem)) {
            return ActionResult.PASS;
        } else {
            DyeableItem dyeableItem = (DyeableItem)item;

            if (!world.isClient) {
                if (!dyeableItem.hasColor(stack)) {
                    float[] colorComp = DyeColor.byId(state.get(DyeableCauldron.DYE_COLOR)).getColorComponents();

                    int color = (int)(colorComp[0] * 255) << 16 | (int)(colorComp[1] * 255) << 8 | (int)(colorComp[2] * 255);

                    dyeableItem.setColor(stack, color);
                } else{
                    DyeableItemExt.blendItemColorAndDyeColor(stack, List.of(DyeColor.byId(state.get(DyeableCauldron.DYE_COLOR))));
                    dyeableItem.removeColor(stack);
                    player.incrementStat(Stats.CLEAN_ARMOR);
                    LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                }
            }

            return ActionResult.success(world.isClient);

        }
    };
}
