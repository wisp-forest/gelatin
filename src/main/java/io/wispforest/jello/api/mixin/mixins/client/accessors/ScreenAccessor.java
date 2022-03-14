package io.wispforest.jello.api.mixin.mixins.client.accessors;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Invoker(value = "renderTooltipFromComponents")
    void jello$renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y);

    @Invoker(value = "renderTooltip")
    void jello$renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y);
}
