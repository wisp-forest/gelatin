package io.wispforest.jello.mixin.dye.item;

import io.wispforest.jello.api.ducks.DyeTool;
import io.wispforest.jello.api.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.dye.ColorManipulators;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedGoldenAppleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnchantedGoldenAppleItem.class)
public class EnchantedGoldenAppleMixin implements DyeTool {

    @Override
    public ActionResult attemptToDyeEntity(World world, PlayerEntity player, DyeableEntity entity, ItemStack stack, Hand hand) {
        if (player.shouldCancelInteraction()) {
            if(ColorManipulators.rainbowEntityEvent(entity)){
                if(!player.getWorld().isClient) {
                    ColorManipulators.decrementPlayerHandItemCC(player, hand);
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}
