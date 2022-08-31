package io.wispforest.gelatin.dye_entities.mixins;

import io.wispforest.gelatin.dye_entities.ducks.ImplDyeEntityTool;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import net.minecraft.item.DyeItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DyeItem.class)
public class DyeItemMixin implements ImplDyeEntityTool, DyeItemStorage {}
