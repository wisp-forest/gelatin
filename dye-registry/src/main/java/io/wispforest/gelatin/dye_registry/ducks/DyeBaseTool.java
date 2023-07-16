package io.wispforest.gelatin.dye_registry.ducks;

import io.wispforest.gelatin.common.util.ItemFunctions;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface DyeBaseTool {

    default void afterInteraction(PlayerEntity player, Hand hand, DyeColorant dyeColorant){
        ItemFunctions.decrementPlayerHandItemCC(player, hand);
    }

    default void afterInteraction(ItemStack stack, DyeColorant dyeColorant){
        ItemFunctions.emptyAwareDecrement(stack);
    }
}
