package io.wispforest.jello.mixin.dye.item;

import io.wispforest.jello.api.ducks.DyeItemStorage;
import io.wispforest.jello.misc.ducks.JelloDyeItemExtension;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.misc.ducks.SheepDyeColorStorage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(DyeItem.class)
public class DyeItemMixin extends Item implements JelloDyeItemExtension {

    @Mutable @Shadow @Final private static Map<DyeColor, DyeItem> DYES;

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
            this.setDyeColor(DyeColorant.byOldDyeColor(((DyeItem) (Object) this).getColor()));

            if (this.getDyeColorant() == null) {
                this.setDyeColor(DyeColorant.byName(color.getName(), DyeColorantRegistry.NULL_VALUE_NEW));
            }
        }
    }

    /**
     * @author Dragon_Seeker
     */
    @Overwrite
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
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
}
