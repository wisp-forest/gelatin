package io.wispforest.gelatin.dye_entries.mixins;

import io.wispforest.gelatin.dye_entries.ducks.ImplDyeItemBlockTool;
import io.wispforest.gelatin.dye_entries.ducks.SheepDyeColorStorage;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DyeItem.class)
public abstract class DyeItemMixin extends Item implements ImplDyeItemBlockTool, DyeItemStorage {

    public DyeItemMixin(Settings settings) {
        super(settings);
    }

    /**
     * @author Dragon_Seeker
     * @reason As it really makes no sense not too
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

    @Override
    public boolean isDyeItem() {
        return true;
    }
}
