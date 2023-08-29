package io.wispforest.gelatin.common.client.util;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class WrappedKeyBinding extends KeyBinding {

    private static final KeyBinding INVALID_KEYBINDING = new KeyBinding("", GLFW.GLFW_KEY_UNKNOWN, "");

    private Supplier<KeyBinding> wrappedKeybinding = null;

    private boolean observationOnly = false;

    public WrappedKeyBinding(KeyBinding wrappedKeybinding) {
        this(() -> wrappedKeybinding);
    }

    public WrappedKeyBinding(Supplier<KeyBinding> wrappedKeybindingSup) {
        super("", 0, "");

        this.wrappedKeybinding = wrappedKeybindingSup;
    }

    public WrappedKeyBinding keybinding(KeyBinding keyBinding){
        return this.keybinding(() -> keyBinding);
    }

    public WrappedKeyBinding keybinding(Supplier<KeyBinding> keyBindingSup){
        this.wrappedKeybinding = keyBindingSup;

        return this;
    }

    public KeyBinding keybinding(){
        //Specifically here to prevent crash with mods mixing into keybind to circumvent issues with vanillas key handling
        if(wrappedKeybinding == null) return INVALID_KEYBINDING;

        return this.wrappedKeybinding.get();
    }

    /**
     * Should only be used when a keybinding is being used somewhere else leading to issues if
     * such will be reset somewhere else also (i.e. {@link KeyBinding#wasPressed()}
     */
    public WrappedKeyBinding observationOnly(boolean value){
        this.observationOnly = value;

        return this;
    }

    public boolean observationOnly(){
        return this.observationOnly;
    }

    @Override
    public boolean isPressed() {
        return keybinding().isPressed();
    }

    @Override
    public String getCategory() {
        return keybinding().getCategory();
    }

    @Override
    public boolean wasPressed() {
        return keybinding().wasPressed();
    }

    @Override
    public String getTranslationKey() {
        return keybinding().getTranslationKey();
    }

    @Override
    public InputUtil.Key getDefaultKey() {
        return keybinding().getDefaultKey();
    }

    @Override
    public void setBoundKey(InputUtil.Key boundKey) {
        keybinding().setBoundKey(boundKey);
    }

    @Override
    public int compareTo(KeyBinding other) {
        if(other instanceof WrappedKeyBinding otherWrapped){
            return otherWrapped.keybinding().compareTo(this.keybinding());
        }

        return 0;
    }

    @Override
    public boolean equals(KeyBinding other) {
        if(other instanceof WrappedKeyBinding otherWrapped){
            return otherWrapped.keybinding().equals(this.keybinding());
        }

        return false;
    }

    @Override
    public boolean isUnbound() {
        return keybinding().isUnbound();
    }

    @Override
    public boolean matchesKey(int keyCode, int scanCode) {
        return keybinding().matchesKey(keyCode, scanCode);
    }

    @Override
    public boolean matchesMouse(int code) {
        return keybinding().matchesMouse(code);
    }

    @Override
    public Text getBoundKeyLocalizedText() {
        return keybinding().getBoundKeyLocalizedText();
    }

    @Override
    public boolean isDefault() {
        return keybinding().isDefault();
    }

    @Override
    public String getBoundKeyTranslationKey() {
        return keybinding().getBoundKeyTranslationKey();
    }

    @Override public void setPressed(boolean pressed) {}
}
