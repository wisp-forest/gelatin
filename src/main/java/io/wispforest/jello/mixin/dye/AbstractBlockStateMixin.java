package io.wispforest.jello.mixin.dye;

import io.wispforest.jello.api.ducks.DyeTool;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow public abstract Block getBlock();

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void testForDyeOrDyeBundle(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.getItem() instanceof DyeTool) {
            if (DyeableBlockVariant.getVariantFromBlock(this.getBlock()) != null) {
                cir.setReturnValue(ActionResult.PASS);
            }
        }
    }
}
