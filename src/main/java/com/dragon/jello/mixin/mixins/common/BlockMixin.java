package com.dragon.jello.mixin.mixins.common;

import com.dragon.jello.common.effects.JelloStatusEffectsRegistry;
import com.dragon.jello.mixin.ducks.BounceEffectMethod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "onLandedUpon", at = @At(value = "HEAD"), cancellable = true)
    private void bounceEffectOnLanded(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci){
        if(!entity.bypassesLandingEffects()) {
            if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(JelloStatusEffectsRegistry.BOUNCE)) {
                entity.handleFallDamage(fallDistance, 0.0F, DamageSource.FALL);
                ci.cancel();
            }
        }
    }

    @Inject(method = "onEntityLand", at = @At(value = "HEAD"), cancellable = true)
    private void bounceEffectOnEntityLand(BlockView world, Entity entity, CallbackInfo ci){
        if(!entity.bypassesLandingEffects()) {
            if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(JelloStatusEffectsRegistry.BOUNCE)) {
                BounceEffectMethod.bounce(livingEntity);
                ci.cancel();
            }
        }
    }
}
