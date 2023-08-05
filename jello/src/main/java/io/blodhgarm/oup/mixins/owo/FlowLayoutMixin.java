package io.blodhgarm.oup.mixins.owo;

import io.blodhgarm.oup.additions.button.ButtonAddon;
import io.wispforest.jello.misc.pond.owo.ButtonAddonDuck;
import io.wispforest.jello.misc.pond.owo.FocusCheck;
import io.wispforest.jello.misc.pond.owo.FocusCheckable;
import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = FlowLayout.class, remap = false)
public abstract class FlowLayoutMixin extends BaseParentComponent implements FocusCheckable, ButtonAddonDuck<FlowLayout> {

    //----------------------------

    private final EventStream<FocusCheck> allowFocusEvents = FocusCheck.newStream();

    //----------------------------

    private ButtonAddon<FlowLayout> buttonAddon = null;

    //----------------------------

    protected FlowLayoutMixin(Sizing horizontalSizing, Sizing verticalSizing) {
        super(horizontalSizing, verticalSizing);
    }

    //----------------------------

    @Override
    public boolean canFocus(FocusSource source) {
        return allowFocusEvents.sink().allowFocusSource(source);
    }

    @Override
    public EventSource<FocusCheck> focusCheck() {
        return allowFocusEvents.source();
    }

    //----------------------------

    @Inject(method = "draw", at = @At("HEAD"))
    private void beforeRender(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta, CallbackInfo ci){
        if(buttonAddon != null) buttonAddon.beforeDraw(context, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if(buttonAddon != null && buttonAddon.onKeyPress(keyCode, scanCode, modifiers)) return true;

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if(buttonAddon != null && buttonAddon.onMouseDown(mouseX, mouseY, button)) return true;

        return super.onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public void dismount(DismountReason reason) {
        super.dismount(reason);

        if(buttonAddon != null && reason != DismountReason.LAYOUT_INFLATION){
            buttonAddon.dismount(reason);
        }
    }

    @Override
    public FlowLayout setButtonAddon(Function<FlowLayout, ButtonAddon<FlowLayout>> addonBuilder) {
        this.buttonAddon = addonBuilder.apply((FlowLayout) (Object) this);

        return (FlowLayout) (Object) this;
    }

    @Override
    @Nullable
    public ButtonAddon<FlowLayout> getAddon() {
        return buttonAddon;
    }
}
