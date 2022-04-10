package io.wispforest.jello.mixin.entitycolor;

import io.wispforest.jello.api.ducks.DyeTool;
import io.wispforest.jello.api.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.dye.ColorManipulators;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BucketItem.class)
public abstract class WaterBucketMixin extends Item implements DyeTool {

    @Shadow
    protected abstract void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos);

    public WaterBucketMixin(Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult attemptToDyeEntity(World world, PlayerEntity player, DyeableEntity entity, ItemStack stack, Hand hand) {
        if (ColorManipulators.washEntityEvent(entity)) {
            if (!player.world.isClient) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, Items.BUCKET.getDefaultStack()));
            }

            this.playEmptyingSound(player, world, player.getBlockPos());

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }
}
