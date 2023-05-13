package io.wispforest.jello.compat;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class JelloConfig extends ConfigWrapper<io.wispforest.jello.compat.JelloConfigModel> {

    private final Option<java.lang.Boolean> addCustomJsonColors = this.optionForKey(new Option.Key("addCustomJsonColors"));
    private final Option<java.lang.Boolean> allowVanillaColorsInPaintMixer = this.optionForKey(new Option.Key("allowVanillaColorsInPaintMixer"));
    private final Option<io.wispforest.jello.compat.JelloConfigModel.HudPosition> bundlePosition = this.optionForKey(new Option.Key("bundlePosition"));

    private JelloConfig() {
        super(io.wispforest.jello.compat.JelloConfigModel.class);
    }

    public static JelloConfig createAndLoad() {
        var wrapper = new JelloConfig();
        wrapper.load();
        return wrapper;
    }

    public boolean addCustomJsonColors() {
        return addCustomJsonColors.value();
    }

    public void addCustomJsonColors(boolean value) {
        addCustomJsonColors.set(value);
    }

    public boolean allowVanillaColorsInPaintMixer() {
        return allowVanillaColorsInPaintMixer.value();
    }

    public void allowVanillaColorsInPaintMixer(boolean value) {
        allowVanillaColorsInPaintMixer.set(value);
    }

    public io.wispforest.jello.compat.JelloConfigModel.HudPosition bundlePosition() {
        return bundlePosition.value();
    }

    public void bundlePosition(io.wispforest.jello.compat.JelloConfigModel.HudPosition value) {
        bundlePosition.set(value);
    }




}

