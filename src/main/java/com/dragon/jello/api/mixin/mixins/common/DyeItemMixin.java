package com.dragon.jello.api.mixin.mixins.common;

import com.dragon.jello.main.common.Jello;
import com.dragon.jello.main.common.data.tags.JelloTags;
import com.dragon.jello.api.mixin.ducks.DyeRedirect;
import com.dragon.jello.api.events.ColorBlockUtil;
import com.dragon.jello.api.events.ColorEntityEvent;
import com.dragon.jello.api.registry.ColorBlockRegistry;
import com.dragon.jello.api.registry.ColorizeRegistry;
import com.dragon.jello.api.mixin.ducks.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DyeItem.class)
public abstract class DyeItemMixin implements DyeRedirect {

    public ActionResult useOnBlock(ItemUsageContext context) {
        if(!Jello.MAIN_CONFIG.enableDyeingBlocks || !this.getDyeColor().getRegistryEntry().isIn(JelloTags.DyeColor.VANILLA_DYES)) {
            return ActionResult.PASS;
        }

        PlayerEntity player = context.getPlayer();

        if (player.shouldCancelInteraction()) {
            World world = context.getWorld();
            BlockState blockState = world.getBlockState(context.getBlockPos());

            if(!ColorBlockUtil.changeBlockColor(world, context.getBlockPos(), blockState, ColorBlockRegistry.getVariant(blockState.getBlock(), this.getDyeColor()), player)){
                return ActionResult.FAIL;
            }

            world.playSound(player, context.getBlockPos(), blockState.getBlock().getSoundGroup(blockState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

            Random random = new Random();
            if(random.nextInt(10) == 0){
                ColorBlockUtil.decrementPlayerHandItemCC(player, context.getHand());
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Inject(method = "useOnEntity", at = @At(value = "HEAD"), cancellable = true)
    private void useOnEntity_ColorEntityEvent(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        if(!Jello.MAIN_CONFIG.enableDyeingEntitys || (entity instanceof PlayerEntity && !Jello.MAIN_CONFIG.enableDyeingPlayers)){
            return;
        }

        if(ColorizeRegistry.isRegistered(entity)){
            if((entity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored())){
                cir.setReturnValue(ActionResult.PASS);
            }

            if(user.shouldCancelInteraction()) {
                if(entity instanceof DyeableEntity dyeableEntity){
                    cir.setReturnValue(ColorEntityEvent.dyeEntityEvent(user, hand, dyeableEntity, this.getDyeColor()));
                }
            }
        }
    }
}
