package io.wispforest.jello.api.mixin.mixins.cauldron;

import io.wispforest.jello.api.events.CauldronEvent;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin  {

    @Shadow @Final private Map<Item, CauldronBehavior> behaviorMap;

    @Inject(method = "onUse", at = @At(value = "HEAD"), cancellable = true)
    private void runDyeableBlockColoring(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir){
        CauldronEvent.CauldronType cauldronType = CauldronEvent.CauldronType.getCauldronType(behaviorMap);

        if(cauldronType != null){
            ActionResult result = CauldronEvent.BEFORE_CAULDRON_BEHAVIOR.invoker().interact(state, world, pos, player, hand, player.getStackInHand(hand), cauldronType);

            if(result != ActionResult.PASS){
                cir.setReturnValue(result);
            }
        }
    }
}
