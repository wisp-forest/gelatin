package io.wispforest.jello.compat;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class JelloConfig extends ConfigWrapper<io.wispforest.jello.compat.JelloConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Boolean> addCustomJsonColors = this.optionForKey(this.keys.addCustomJsonColors);
    private final Option<java.lang.Boolean> allowVanillaColorsInPaintMixer = this.optionForKey(this.keys.allowVanillaColorsInPaintMixer);
    private final Option<io.wispforest.jello.compat.JelloConfigModel.HudPosition> bundlePosition = this.optionForKey(this.keys.bundlePosition);

    private JelloConfig() {
        super(io.wispforest.jello.compat.JelloConfigModel.class);
    }

    private JelloConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(io.wispforest.jello.compat.JelloConfigModel.class, janksonBuilder);
    }

    public static JelloConfig createAndLoad() {
        var wrapper = new JelloConfig();
        wrapper.load();
        return wrapper;
    }

    public static JelloConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new JelloConfig(janksonBuilder);
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


    public static class Keys {
        public final Option.Key addCustomJsonColors = new Option.Key("addCustomJsonColors");
        public final Option.Key allowVanillaColorsInPaintMixer = new Option.Key("allowVanillaColorsInPaintMixer");
        public final Option.Key bundlePosition = new Option.Key("bundlePosition");
    }
}

