package io.wispforest.jello.api.mixin.mixins.dye;

import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.events.ColorBlockUtil;
import io.wispforest.jello.api.events.ColorEntityEvent;
import io.wispforest.jello.api.mixin.ducks.ConstantColorEntity;
import io.wispforest.jello.api.mixin.ducks.DyeRedirect;
import io.wispforest.jello.api.mixin.ducks.DyeableEntity;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.api.registry.ColorizeRegistry;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.common.data.tags.JelloTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DyeItem.class)
public class DyeItemMixin extends Item implements DyeRedirect {

    @Unique @Mutable @Final private DyeColorant color;

    public DyeItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void fillNewDyeMap(net.minecraft.util.DyeColor color, Item.Settings settings, CallbackInfo ci){
        if(color != DyeColorRegistry.NULL_VALUE_OLD) {
            this.color = DyeColorant.byOldDyeColor(((DyeItem) (Object) this).getColor());

            if(this.color == null){
                DyeColorant possibleColor = DyeColorant.byName(color.getName(), null);

                if(possibleColor != null){
                    this.color = possibleColor;
                }
            }
        }

        DyeColorRegistry.DYE_COLOR_TO_DYEITEM.put(this.color, (DyeItem)(Object)this);
    }

    @Override
    public DyeColorant getDyeColor() {
        return color;
    }

    //-------------------------------------------------------------------

    @Override
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
