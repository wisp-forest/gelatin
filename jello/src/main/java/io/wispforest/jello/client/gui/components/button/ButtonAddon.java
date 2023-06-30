package io.wispforest.jello.client.gui.components.button;

import io.wispforest.jello.misc.pond.owo.FocusCheckable;
import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Surface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class ButtonAddon<T extends BaseParentComponent> {

    protected ButtonSurface renderer = null;

    protected boolean checkThemeMode = true;
    protected boolean squareMode = true;

    protected boolean hovered = false;
    protected boolean active = true;

    protected PressAction<T> onPress = button -> {};

    protected final T linkedComponent;

    public ButtonAddon(T component) {
        component.mouseEnter().subscribe(() -> this.hovered = true);
        component.mouseLeave().subscribe(() -> this.hovered = false);

        if(component instanceof FocusCheckable focusCheckable) focusCheckable.focusCheck().subscribe(source -> active);

        this.linkedComponent = component;
    }

    public ButtonAddon<T> onPress(PressAction<T> onPress) {
        this.onPress = onPress;

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

    public ButtonAddon<T> useCustomButtonSurface(ButtonSurface renderer){
        this.renderer = renderer;

        var buttonSurface = new ImplButtonSurface(this, renderer);

        this.linkedComponent.surface(buttonSurface);

        return this;
    }

    public static int getVIndex(Boolean darkMode, Boolean squareShape){
        return ((squareShape) ? 1 : 0) + (((darkMode) ? 1 : 0) << 1);
    }

    public void beforeDraw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
    }

    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (this.active) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.onPress.onPress(linkedComponent);
                return true;
            }
        }

        return false;
    }

    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (this.active) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.onPress.onPress(linkedComponent);
                return true;
            }
        }

        return false;
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction<T extends Component> {
        void onPress(T button);
    }

    private record ImplButtonSurface(ButtonAddon<?> addon, ButtonSurface renderer) implements Surface {
        @Override
        public void draw(MatrixStack matrices, ParentComponent component) {
            renderer.draw(addon, matrices, component);
        }
    }

}
