package io.wispforest.jello.api.events.behavior.cauldron;

import io.wispforest.jello.api.mixin.ducks.DyeRedirect;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.jello.main.common.items.SpongeItem;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.events.ColorBlockUtil;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.api.mixin.ducks.DyeableCauldron;
import io.wispforest.jello.api.mixin.ducks.DyeableItemExt;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public interface JelloCauldronBehaviors extends CauldronBehavior {

    static void registerJelloBehavior() {
        for(DyeColorant dyeColor : DyeColorRegistry.VANILLA_DYES){
            Item dyeItem = Registry.ITEM.get(new Identifier(dyeColor.getName() + "_dye"));

            CauldronBehavior colorWater = (state, world, pos, player, hand, stack) -> changeColor((DyeItem)dyeItem,
                    world,
                    pos,
                    player,
                    hand,
                    stack,
                    state,
                    SoundEvents.ITEM_BUCKET_EMPTY
            );

            WATER_CAULDRON_BEHAVIOR.put(dyeItem, colorWater);
            //WATER_CAULDRON_BEHAVIOR.put(Registry.ITEM.get(new Identifier(dyeColor.getName() + "_shulker_box")), DYE_SHULKER_BOX);
        }

        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_BOOTS, DYE_OR_CLEAN_DYEABLE_ITEM);
        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_LEGGINGS, DYE_OR_CLEAN_DYEABLE_ITEM);
        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_CHESTPLATE, DYE_OR_CLEAN_DYEABLE_ITEM);
        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_HELMET, DYE_OR_CLEAN_DYEABLE_ITEM);
        WATER_CAULDRON_BEHAVIOR.put(Items.LEATHER_HORSE_ARMOR, DYE_OR_CLEAN_DYEABLE_ITEM);

        WATER_CAULDRON_BEHAVIOR.put(null, DYE_BLOCK_ITEM);

        WATER_CAULDRON_BEHAVIOR.put(ItemRegistry.MainItemRegistry.SPONGE, CLEAN_SPONGE);
    }

    static ActionResult changeColor(DyeItem dyeItem, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent) {
        if(DyeableCauldron.isWaterColored(state) && ((DyeRedirect)dyeItem).getDyeColor() == DyeableCauldron.getDyeColor(state)){
            return ActionResult.PASS;
        }

        BlockState coloredState = Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, state.get(LeveledCauldronBlock.LEVEL)).with(DyeableCauldron.DYE_COLOR, (dyeItem).getColor().getId());

        if (!world.isClient) {
            Item item = stack.getItem();
            if(!player.getAbilities().creativeMode){
                player.getMainHandStack().decrement(1);
            }
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            world.setBlockState(pos, coloredState);
            world.playSound((PlayerEntity)null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
        }

        return ActionResult.success(world.isClient);
    }

    CauldronBehavior CLEAN_SPONGE = (state, world, pos, player, hand, stack) -> {
        if(DyeableCauldron.isWaterColored(state)){
            return ActionResult.PASS;
        }

        if(stack.getOrCreateNbt().getInt(SpongeItem.DIRTINESS_KEY) != 0){
            if(!world.isClient) {
                stack.getOrCreateNbt().putInt(SpongeItem.DIRTINESS_KEY, 0);

                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);

                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.4F);
            }
            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    };

    CauldronBehavior CLEAN_BLOCK_ITEM = (state, world, pos, player, hand, stack) -> {
        Block block = Block.getBlockFromItem(stack.getItem());

        if(block != Blocks.AIR) {
            Block changedBlock = ColorBlockRegistry.getVariant(block, null);

            if (!ColorBlockUtil.changeBlockItemColor(world, pos, stack, changedBlock, player, hand, true)) {
                return player.shouldCancelInteraction() ? ActionResult.FAIL : ActionResult.CONSUME_PARTIAL;
            }

            if (!world.isClient) {
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);

                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.4F);
            }

            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    };

    CauldronBehavior DYE_BLOCK_ITEM = (state, world, pos, player, hand, stack) -> {
        Block block = Block.getBlockFromItem(stack.getItem());
        if(block == Blocks.AIR){
            return ActionResult.PASS;
        }

        Block changedBlock = ColorBlockRegistry.getVariant(block, DyeableCauldron.getDyeColor(state));
        if(changedBlock == null){
            return ActionResult.FAIL;
        }

        if(DyeableCauldron.isWaterColored(state)) {
            if (!ColorBlockUtil.changeBlockItemColor(world, pos, stack, changedBlock, player, hand, true)) {
                return player.shouldCancelInteraction() ? ActionResult.FAIL : ActionResult.CONSUME_PARTIAL;
            }

            if (!world.isClient) {
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);

                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.4F);
            }

            return ActionResult.success(world.isClient);
        }else{
            return CLEAN_BLOCK_ITEM.interact(state, world, pos, player, hand, stack);
        }
    };

    CauldronBehavior DYE_OR_CLEAN_DYEABLE_ITEM = (state, world, pos, player, hand, stack) -> {
        Item item = stack.getItem();
        if (item instanceof DyeableItem dyeableItem && DyeableCauldron.isWaterColored(state)) {
            if (!world.isClient) {
                if (!dyeableItem.hasColor(stack)) {
                    float[] colorComp = net.minecraft.util.DyeColor.byId(state.get(DyeableCauldron.DYE_COLOR)).getColorComponents();

                    int color = (int) (colorComp[0] * 255) << 16 | (int) (colorComp[1] * 255) << 8 | (int) (colorComp[2] * 255);

                    dyeableItem.setColor(stack, color);
                } else {
                    player.setStackInHand(hand, DyeableItemExt.blendItemColorAndDyeColor(stack, List.of(net.minecraft.util.DyeColor.byId(state.get(DyeableCauldron.DYE_COLOR)))));
                }

                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                player.incrementStat(Jello.Stats.DYE_ARMOR);

                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.4F);
            }

            return ActionResult.success(world.isClient);
        }else{
            return CLEAN_DYEABLE_ITEM.interact(state, world, pos, player, hand, stack);
        }
    };
}
