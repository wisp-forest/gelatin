package com.dragon.jello.mixin.mixins.common;

import com.dragon.jello.mixin.ducks.DyeableItemExt;
import net.minecraft.item.DyeableItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DyeableItem.class)
public interface DyeableItemMixin extends DyeableItemExt {
}
