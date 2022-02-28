package com.dragon.jello.mixin.mixins.common;

import com.dragon.jello.common.Jello;
import com.dragon.jello.lib.dyecolor.DyeColorRegistry;
import com.dragon.jello.lib.events.ColorBlockUtil;
import com.dragon.jello.lib.events.ColorEntityEvent;
import com.dragon.jello.lib.registry.ColorBlockRegistry;
import com.dragon.jello.lib.registry.ColorizeRegistry;
import com.dragon.jello.mixin.ducks.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DyeItem.class)
public abstract class DyeItemMixin implements DyeItemRedirect {

    @Unique @Mutable @Final private DyeColorRegistry.DyeColor color;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void fillNewDyeMap(DyeColor color, Item.Settings settings, CallbackInfo ci){
        this.color = DyeColorRegistry.DyeColor.byOldDyeColor(((DyeItem)(Object)this).getColor());

        DyeColorRegistry.DYE_COLOR_TO_DYEITEM.put(this.color, (DyeItem)(Object)this);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        if(!Jello.MAIN_CONFIG.enableDyeingBlocks){
            return ActionResult.PASS;
        }

        PlayerEntity player = context.getPlayer();

        if (player.shouldCancelInteraction()) {
            World world = context.getWorld();
            BlockState blockState = world.getBlockState(context.getBlockPos());

            if(!ColorBlockUtil.changeBlockColor(world, context.getBlockPos(), blockState, ColorBlockRegistry.getVariant(blockState.getBlock(), ((DyeItem)(Object)this).getColor()), player)){
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
            if(entity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored() && !(entity instanceof SheepEntity)){
                cir.setReturnValue(ActionResult.PASS);
            }

            if(user.shouldCancelInteraction()) {
                if(entity instanceof DyeableEntity dyeableEntity){
                    cir.setReturnValue(ColorEntityEvent.dyeEntityEvent(user, hand, dyeableEntity, ((DyeItem)(Object)this)));
                }
            }
        }
    }

    @Override
    public DyeColorRegistry.DyeColor getDyeColor() {
        return color;
    }
}
