package io.wispforest.dye_entities.mixins;

import io.wispforest.dye_entities.ducks.ImplDyeEntityTool;
import io.wispforest.dye_registry.ducks.DyeItemStorage;
import net.minecraft.item.DyeItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DyeItem.class)
public class DyeItemMixin implements ImplDyeEntityTool, DyeItemStorage {}
