package io.wispforest.dye_entities.mixins;

import io.wispforest.dye_entities.ducks.DyeableEntity;
import io.wispforest.dye_entities.misc.EntityColorManipulators;
import io.wispforest.dye_registry.ducks.DyeEntityTool;
import net.minecraft.entity.LivingEntity;
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
public abstract class WaterBucketMixin extends Item implements DyeEntityTool {

    @Shadow
    protected abstract void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos);

    public WaterBucketMixin(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult attemptToDyeEntity(World world, PlayerEntity player, LivingEntity entity, ItemStack stack, Hand hand) {
        if (EntityColorManipulators.washEntityEvent((DyeableEntity) entity)) {
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
