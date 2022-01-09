package com.dragon.jello.events;

import com.dragon.jello.mixin.ducks.DyeableEntity;
import com.dragon.jello.mixin.ducks.RainbowEntity;
import com.dragon.jello.registry.ColorizeRegistry;
import io.wispforest.owo.ops.ItemOps;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Blocks;
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
        if(ColorizeRegistry.isRegistered(entity)){
            Item mainHandItem = player.getMainHandStack().getItem();

            if(player.shouldCancelInteraction() && entity instanceof LivingEntity livingEntity) {
                if(livingEntity instanceof DyeableEntity dyeableEntity && mainHandItem instanceof DyeItem dyeItem){
                    return dyeEntityEvent(player, hand, dyeableEntity, dyeItem);
                }
                else if(livingEntity instanceof RainbowEntity rainbowEntity && mainHandItem instanceof EnchantedGoldenAppleItem) {
                    return rainbowEntityEvent(player, hand, rainbowEntity);
                }
                else if (mainHandItem instanceof BucketItem bucketItem) {
                    return washEntityEvent(player, livingEntity, player.getMainHandStack(), bucketItem, false);
                }
                else if (mainHandItem == Blocks.WET_SPONGE.asItem()) {
                    return washEntityEvent(player, livingEntity, player.getMainHandStack(), null, true);
                }
            }
        }

        return ActionResult.PASS;
    }

    private ActionResult dyeEntityEvent(PlayerEntity player, Hand hand, DyeableEntity dyeableEntity, DyeItem dyeItem){
        if ((dyeableEntity.isRainbowTime()) || dyeItem.getColor().getId() == dyeableEntity.getDyeColorID()) {
            return ActionResult.FAIL;
        }

        dyeableEntity.setDyeColorID(dyeItem.getColor().getId());
        decrementPlayerHandItemCC(player, hand);

        return ActionResult.SUCCESS;
    }

    private ActionResult rainbowEntityEvent(PlayerEntity player, Hand hand, RainbowEntity rainbowEntity){
        if(rainbowEntity.isRainbowTime() || (rainbowEntity instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed())){
            return ActionResult.FAIL;
        }

        rainbowEntity.setRainbowTime(true);
        decrementPlayerHandItemCC(player, hand);

        return ActionResult.SUCCESS;
    }

    private ActionResult washEntityEvent(PlayerEntity player, LivingEntity livingEntity, ItemStack mainHandStack, @Nullable BucketItem bucketItem, boolean spongeItemUsed){
        boolean washedEntity = false;

        if ((bucketItem != null && bucketItem.fluid == Fluids.WATER) || spongeItemUsed) {
            if(livingEntity instanceof DyeableEntity dyeableEntity){
                if(dyeableEntity.isDyed()){
                    dyeableEntity.setDyeColorID(16);
                    washedEntity = true;
                }
            }
            if(livingEntity instanceof RainbowEntity rainbowEntity){
                if(rainbowEntity.isRainbowTime()){
                    rainbowEntity.setRainbowTime(false);
                    washedEntity = true;
                }
            }

            if(washedEntity){
                if(bucketItem != null){
                    player.playSound(SoundEvents.ITEM_BUCKET_EMPTY, 1.0F, 1.0F);
                    if(!player.getAbilities().creativeMode){
                        ItemUsage.exchangeStack(mainHandStack, player, BucketItem.getEmptiedStack(mainHandStack, player));
                    }
                }else{
                    player.playSound(SoundEvents.ITEM_BUCKET_EMPTY, 1.0F, 1.55F);
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    private void decrementPlayerHandItemCC(PlayerEntity player, Hand hand){
        if(!player.getAbilities().creativeMode){
            ItemOps.decrementPlayerHandItem(player, hand);
        }
    }
}
