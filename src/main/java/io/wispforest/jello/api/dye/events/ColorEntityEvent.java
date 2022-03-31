package io.wispforest.jello.api.dye.events;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.item.dyebundle.DyeBundleItem;
import io.wispforest.jello.misc.ducks.entity.ConstantColorEntity;
import io.wispforest.jello.misc.ducks.entity.DyeableEntity;
import io.wispforest.jello.misc.ducks.entity.RainbowEntity;
import io.wispforest.jello.api.registry.ColorizeRegistry;
import io.wispforest.owo.ops.ItemOps;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ColorEntityEvent implements UseEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (ColorizeRegistry.isRegistered(entity)) {
            Item mainHandItem = player.getMainHandStack().getItem();

            if (entity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()) {
                return ActionResult.PASS;
            }

            if (player.shouldCancelInteraction() && entity instanceof LivingEntity livingEntity) {
                if (livingEntity instanceof RainbowEntity rainbowEntity && mainHandItem instanceof EnchantedGoldenAppleItem) {
                    return rainbowEntityEvent(player, hand, rainbowEntity);
                }
            }
        }

        return ActionResult.PASS;
    }

    public static ActionResult dyeEntityEvent(PlayerEntity player, Hand hand, DyeableEntity dyeableEntity, DyeColorant dyeColor) {
        if ((dyeableEntity.isRainbowTime()) || dyeColor == dyeableEntity.getDyeColor()) {//|| dyeItem.getColor().getId() == DyeColor.WHITE.getId()) {
            return ActionResult.FAIL;
        }

        dyeableEntity.setDyeColor(dyeColor);

        if (!player.getAbilities().creativeMode) {
            if (player.getStackInHand(hand).getItem() instanceof DyeBundleItem) {
                DyeBundleItem.dyeBundleInteraction(player.getStackInHand(hand), dyeColor);
            } else {
                decrementPlayerHandItemCC(player, hand);
            }
        }

        return ActionResult.SUCCESS;
    }

    public static ActionResult rainbowEntityEvent(PlayerEntity player, Hand hand, RainbowEntity rainbowEntity) {
        if (rainbowEntity.isRainbowTime() || (rainbowEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed())) {
            return ActionResult.FAIL;
        }

        rainbowEntity.setRainbowTime(true);
        decrementPlayerHandItemCC(player, hand);

        return ActionResult.SUCCESS;
    }

    public static boolean washEntityEvent(PlayerEntity player, LivingEntity livingEntity, ItemStack mainHandStack) {
        boolean washedEntity = false;

        if (livingEntity instanceof DyeableEntity dyeableEntity) {
            if (dyeableEntity.isDyed()) {
                dyeableEntity.setDyeColor(DyeColorantRegistry.NULL_VALUE_NEW);
                washedEntity = true;
            }
        }
        if (livingEntity instanceof RainbowEntity rainbowEntity) {
            if (rainbowEntity.isRainbowTime()) {
                rainbowEntity.setRainbowTime(false);
                washedEntity = true;
            }
        }

        return washedEntity;
    }

    public static ActionResult washEntityEvent(PlayerEntity player, LivingEntity livingEntity, ItemStack mainHandStack, @Nullable BucketItem bucketItem, boolean spongeItemUsed) {
        boolean washedEntity = false;

        if ((bucketItem != null && bucketItem.fluid == Fluids.WATER) || spongeItemUsed) {
            if (livingEntity instanceof DyeableEntity dyeableEntity) {
                if (dyeableEntity.isDyed()) {
                    dyeableEntity.setDyeColor(DyeColorantRegistry.NULL_VALUE_NEW);
                    washedEntity = true;
                }
            }
            if (livingEntity instanceof RainbowEntity rainbowEntity) {
                if (rainbowEntity.isRainbowTime()) {
                    rainbowEntity.setRainbowTime(false);
                    washedEntity = true;
                }
            }

            if (washedEntity) {
                if (bucketItem != null) {
                    player.playSound(SoundEvents.ITEM_BUCKET_EMPTY, 1.0F, 1.0F);
                    if (!player.getAbilities().creativeMode) {
                        ItemUsage.exchangeStack(mainHandStack, player, BucketItem.getEmptiedStack(mainHandStack, player));
                    }
                } else {
                    player.playSound(SoundEvents.ITEM_BUCKET_EMPTY, 1.0F, 1.55F);
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    private static void decrementPlayerHandItemCC(PlayerEntity player, Hand hand) {
        if (!player.getAbilities().creativeMode) {
            ItemOps.decrementPlayerHandItem(player, hand);
        }
    }


}
