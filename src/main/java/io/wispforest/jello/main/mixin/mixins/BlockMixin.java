package io.wispforest.jello.main.mixin.mixins;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class BlockMixin {

//    @Inject(method = "onLandedUpon", at = @At(value = "HEAD"), cancellable = true)
//    private void bounceEffectOnLanded(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci){
//        if(!entity.bypassesLandingEffects()) {
//            if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(JelloStatusEffectsRegistry.BOUNCE)) {
//                entity.handleFallDamage(fallDistance, 0.0F, DamageSource.FALL);
//                ci.cancel();
//            }
//        }
//    }
//
//    @Inject(method = "onEntityLand", at = @At(value = "HEAD"), cancellable = true)
//    private void bounceEffectOnEntityLand(BlockView world, Entity entity, CallbackInfo ci){
//        if(!entity.bypassesLandingEffects()) {
//            if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(JelloStatusEffectsRegistry.BOUNCE)) {
//                BounceEffectMethod.bounce(livingEntity);
//                ci.cancel();
//            }
//        }
//    }
}
