package com.dragon.jello.api.mixin.mixins.cauldron;

import com.dragon.jello.api.mixin.ducks.DyeableItemExt;
import net.minecraft.item.DyeableItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DyeableItem.class)
public interface DyeableItemMixin extends DyeableItemExt {
}
