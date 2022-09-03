package io.wispforest.gelatin.dye_entities.mixins;

import io.wispforest.gelatin.dye_entities.ducks.CustomCollarColorStorage;
import io.wispforest.gelatin.dye_entities.ducks.DyeableEntity;
import io.wispforest.gelatin.dye_entities.misc.EntityColorManipulators;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_entities.ducks.DyeEntityTool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedGoldenAppleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnchantedGoldenAppleItem.class)
public class EnchantedGoldenAppleMixin implements DyeEntityTool {

    @Override
    public ActionResult attemptToDyeEntity(World world, PlayerEntity player, LivingEntity entity, ItemStack stack, Hand hand) {
        if (player.shouldCancelInteraction() && entity instanceof DyeableEntity dyeableEntity) {
            if(EntityColorManipulators.rainbowEntityEvent(dyeableEntity)){
                if(!player.getWorld().isClient) {
                    afterInteraction(player, hand, DyeColorantRegistry.NULL_VALUE_NEW);
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult attemptToDyeEntityCollar(World world, PlayerEntity player, Hand hand, CustomCollarColorStorage collarAbleEntity) {
        ItemStack stack = player.getStackInHand(hand);

        if(stack.getItem() instanceof EnchantedGoldenAppleItem enchantedGoldenAppleItem && !collarAbleEntity.isRainbowCollared()){
            if(!world.isClient) {
                collarAbleEntity.setRainbowCollar(true);
                collarAbleEntity.setCustomCollarColor(DyeColorantRegistry.NULL_VALUE_NEW);
            }

            ((DyeEntityTool)enchantedGoldenAppleItem).afterInteraction(player, hand, DyeColorantRegistry.NULL_VALUE_NEW);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
