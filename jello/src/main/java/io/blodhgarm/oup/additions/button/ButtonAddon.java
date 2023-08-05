package io.blodhgarm.oup.additions.button;

import io.wispforest.jello.misc.pond.owo.FocusCheckable;
import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Surface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class ButtonAddon<T extends BaseParentComponent> {

    protected ButtonSurface renderer = null;

    protected boolean checkThemeMode = true;
    protected boolean squareMode = true;

    protected boolean hovered = false;
    protected boolean active = true;

    protected Runnable onPress = () -> {};

    protected final T linkedComponent;

    public ButtonAddon(T component) {
        component.mouseEnter().subscribe(() -> this.hovered = true);
        component.mouseLeave().subscribe(() -> this.hovered = false);

        if(component instanceof FocusCheckable focusCheckable) focusCheckable.focusCheck().subscribe(source -> active);

        this.linkedComponent = component;
    }

    public <A extends ButtonAddon<T>> ButtonAddon<T> onPress(PressAction<T, A> pressAction) {
        this.onPress = () -> pressAction.onPress((A) this);

        return this;
    }

    public ButtonAddon<T> setActive(boolean isActive){
        this.active = isActive;

        return this;
    }

    public ButtonAddon<T> checkThemeMode(boolean value){
        this.checkThemeMode = value;

        return this;
    }

    public ButtonAddon<T> changeButtonShape(boolean useSquareVersion){
        this.squareMode = useSquareVersion;

        return this;
    }

    public boolean isActive(){
        return this.active;
    }

    public boolean isHovered(){
        return this.hovered;
    }

    public ButtonAddon<T> buttonSurface(ButtonSurface renderer){
        this.renderer = renderer;

        var buttonSurface = new ImplButtonSurface(this, renderer);

        this.linkedComponent.surface(buttonSurface);

        return this;
    }

    public static int getVIndex(Boolean darkMode, Boolean squareShape){
        return ((squareShape) ? 1 : 0) + (((darkMode) ? 1 : 0) << 1);
    }

    public void beforeDraw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {}

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        boolean bl = this.active
                && keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_KP_ENTER;

        if (bl) this.onPress();

        return bl;
    }

    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        boolean bl = this.active && button == GLFW.GLFW_MOUSE_BUTTON_1;

        if(bl) this.onPress();

        return bl;
    }

    public void dismount(Component.DismountReason reason) {}

    public void onPress() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

        this.onPress.run();
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction<T extends BaseParentComponent, A extends ButtonAddon<T>> {
        void onPress(A addon);
    }

    private record ImplButtonSurface(ButtonAddon<?> addon, ButtonSurface renderer) implements Surface {
        @Override
        public void draw(OwoUIDrawContext context, ParentComponent component) {
            renderer.draw(addon, context, component);
        }
    }

}
