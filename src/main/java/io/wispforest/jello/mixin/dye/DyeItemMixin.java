package io.wispforest.jello.mixin.dye;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.events.ColorBlockEventMethods;
import io.wispforest.jello.api.dye.events.ColorEntityEvent;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.misc.ducks.DyeItemStorage;
import io.wispforest.jello.misc.ducks.SheepDyeColorStorage;
import io.wispforest.jello.misc.ducks.entity.ConstantColorEntity;
import io.wispforest.jello.misc.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.api.registry.ColorizeRegistry;
import io.wispforest.jello.Jello;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Mixin(DyeItem.class)
public class DyeItemMixin extends Item implements DyeItemStorage {

    @Mutable
    @Shadow
    @Final
    private static Map<DyeColor, DyeItem> DYES;
    @Unique
    @Mutable
    private DyeColorant color;

    public DyeItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void changeDYESmapType(CallbackInfo ci) {
        DYES = new HashMap<>();
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void fillNewDyeMap(net.minecraft.util.DyeColor color, Item.Settings settings, CallbackInfo ci) {
        if (color != DyeColorantRegistry.Constants.NULL_VALUE_OLD) {
            this.color = DyeColorant.byOldDyeColor(((DyeItem) (Object) this).getColor());

            if (this.color == null) {
                this.color = DyeColorant.byName(color.getName(), DyeColorantRegistry.NULL_VALUE_NEW);
            }
        }
    }

    @Override
    public DyeColorant getDyeColorant() {
        return color;
    }

    @Override
    public void setDyeColor(DyeColorant dyeColorant) {
        this.color = dyeColorant;
    }

    //-------------------------------------------------------------------

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!Jello.getConfig().enableDyeingBlocks) {
            return ActionResult.PASS;
        }

        PlayerEntity player = context.getPlayer();

        if (player.shouldCancelInteraction()) {
            World world = context.getWorld();
            BlockState blockState = world.getBlockState(context.getBlockPos());

            if (!ColorBlockEventMethods.changeBlockColor(world, context.getBlockPos(), blockState, ColorBlockRegistry.getVariant(blockState.getBlock(), this.getDyeColorant()), player)) {
                return ActionResult.FAIL;
            }

            world.playSound(player, context.getBlockPos(), blockState.getBlock().getSoundGroup(blockState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!player.getAbilities().creativeMode) {
                Random random = new Random();
                if (random.nextInt(10) == 0) {
                    ColorBlockEventMethods.decrementPlayerHandItemCC(player, context.getHand());
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    /**
     * @author Dragon_Seeker
     */
    @Overwrite
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (Jello.getConfig().enableDyeingEntities || (entity instanceof PlayerEntity && Jello.getConfig().enableDyeingPlayers)) {
            if (ColorizeRegistry.isRegistered(entity)) {
                if (user.shouldCancelInteraction()) {
                    if ((entity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored())) {
                        return ActionResult.PASS;
                    }

                    if (entity instanceof DyeableEntity dyeableEntity) {
                        return ColorEntityEvent.dyeEntityEvent(user, hand, dyeableEntity, this.getDyeColorant());
                    }
                }
            }
        }

        if (entity instanceof SheepEntity sheepEntity) {
            if (sheepEntity.isAlive() && !sheepEntity.isSheared() && ((SheepDyeColorStorage) sheepEntity).getWoolDyeColor() != this.getDyeColorant()) {
                sheepEntity.world.playSoundFromEntity(user, sheepEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!user.world.isClient) {
                    ((SheepDyeColorStorage) sheepEntity).setWoolDyeColor(this.getDyeColorant());
                    stack.decrement(1);
                }

                return ActionResult.success(user.world.isClient);
            }
        }

        return super.useOnEntity(stack, user, entity, hand);
    }

//    @Inject(method = "useOnEntity", at = @At(value = "HEAD"), cancellable = true)
//    private void useOnEntity_ColorEntityEvent(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir){
//
//    }
}