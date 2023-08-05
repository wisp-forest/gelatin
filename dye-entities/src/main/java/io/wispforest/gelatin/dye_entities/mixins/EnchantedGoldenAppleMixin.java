package io.wispforest.gelatin.dye_entities.mixins;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entities.ducks.Colorable;
import io.wispforest.gelatin.dye_entities.ducks.CollarColorable;
import io.wispforest.gelatin.dye_entities.ducks.DyeEntityTool;
import io.wispforest.gelatin.dye_entities.misc.EntityColorImplementations;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedGoldenAppleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnchantedGoldenAppleItem.class)
public class EnchantedGoldenAppleMixin implements DyeEntityTool {

    @Override
    public ActionResult attemptToDyeEntity(World world, PlayerEntity player, LivingEntity entity, ItemStack stack, Hand hand) {
        boolean bl = player.getState(GelatinConstants.DYE_TOGGLE_SYNC_ID)
                && entity instanceof Colorable colorable
                && EntityColorImplementations.rainbowEntityEvent(colorable);

        if (!bl) return ActionResult.PASS;

        if(!player.getWorld().isClient) afterInteraction(player, hand, DyeColorantRegistry.NULL_VALUE_NEW);

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult attemptToDyeEntityCollar(World world, PlayerEntity player, Hand hand, CollarColorable collarAbleEntity) {
        Item item = player.getStackInHand(hand).getItem();

        if(!(item instanceof EnchantedGoldenAppleItem) || collarAbleEntity.isRainbowCollared()) return ActionResult.PASS;

        if(!world.isClient) {
            collarAbleEntity.setRainbowCollar(true);
            collarAbleEntity.setCustomCollarColor(DyeColorantRegistry.NULL_VALUE_NEW);
        }

        ((DyeEntityTool)item).afterInteraction(player, hand, DyeColorantRegistry.NULL_VALUE_NEW);

        return ActionResult.SUCCESS;
    }
}
