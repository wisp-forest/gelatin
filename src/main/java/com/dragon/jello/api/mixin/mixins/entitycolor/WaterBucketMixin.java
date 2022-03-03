package com.dragon.jello.api.mixin.mixins.entitycolor;

import com.dragon.jello.api.events.ColorEntityEvent;
import com.dragon.jello.api.mixin.ducks.ConstantColorEntity;
import com.dragon.jello.api.mixin.ducks.DyeableEntity;
import com.dragon.jello.api.registry.ColorizeRegistry;
import com.dragon.jello.main.common.Jello;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BucketItem.class)
public abstract class WaterBucketMixin extends Item {

    @Shadow protected abstract void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos);

    public WaterBucketMixin(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(!Jello.MAIN_CONFIG.enableDyeingEntitys || (entity instanceof PlayerEntity && !Jello.MAIN_CONFIG.enableDyeingPlayers)){
            return ActionResult.PASS;
        }

        if(stack.getItem() == Items.WATER_BUCKET){
            if(ColorizeRegistry.isRegistered(entity)) {
                if(entity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()){
                    return ActionResult.PASS;
                }

                if (ColorEntityEvent.washEntityEvent(user, entity, user.getMainHandStack())) {
                    if (!user.world.isClient) {
                        user.setStackInHand(hand, ItemUsage.exchangeStack(stack, user, Items.BUCKET.getDefaultStack()));
                    }

                    this.playEmptyingSound(user, entity.getWorld(), entity.getBlockPos());

                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }
        }


        return super.useOnEntity(stack, user, entity, hand);
    }
}
