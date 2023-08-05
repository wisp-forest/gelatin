package io.wispforest.gelatin;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GelatinConfig extends ConfigWrapper<io.wispforest.gelatin.GelatinConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Boolean> enableDyeingEntities = this.optionForKey(this.keys.enableDyeingEntities);
    private final Option<java.lang.Boolean> enableDyeingPlayers = this.optionForKey(this.keys.enableDyeingPlayers);
    private final Option<java.lang.Boolean> enableDyeingBlocks = this.optionForKey(this.keys.enableDyeingBlocks);
    private final Option<java.lang.Boolean> enableTransparencyFixCauldrons = this.optionForKey(this.keys.enableTransparencyFixCauldrons);
    private final Option<java.lang.Boolean> enableGrayScalingOfEntities = this.optionForKey(this.keys.enableGrayScalingOfEntities);
    private final Option<java.lang.Boolean> dyeingControls_useSeparateKeybinding = this.optionForKey(this.keys.dyeingControls_useSeparateKeybinding);
    private final Option<java.lang.Boolean> dyeingControls_useToggleMode = this.optionForKey(this.keys.dyeingControls_useToggleMode);
    private final Option<java.lang.Boolean> dyeingControls_alwaysOnByDefault = this.optionForKey(this.keys.dyeingControls_alwaysOnByDefault);
    private final Option<java.util.List<java.lang.String>> gelatinBlackListModid = this.optionForKey(this.keys.gelatinBlackListModid);

    private GelatinConfig() {
        super(io.wispforest.gelatin.GelatinConfigModel.class);
    }

    private GelatinConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(io.wispforest.gelatin.GelatinConfigModel.class, janksonBuilder);
    }

    public static GelatinConfig createAndLoad() {
        var wrapper = new GelatinConfig();
        wrapper.load();
        return wrapper;
    }

    public static GelatinConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new GelatinConfig(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public boolean enableDyeingEntities() {
        return enableDyeingEntities.value();
    }

    public void enableDyeingEntities(boolean value) {
        enableDyeingEntities.set(value);
    }

    public boolean enableDyeingPlayers() {
        return enableDyeingPlayers.value();
    }

    public void enableDyeingPlayers(boolean value) {
        enableDyeingPlayers.set(value);
    }

    public boolean enableDyeingBlocks() {
        return enableDyeingBlocks.value();
    }

    public void enableDyeingBlocks(boolean value) {
        enableDyeingBlocks.set(value);
    }

    public boolean enableTransparencyFixCauldrons() {
        return enableTransparencyFixCauldrons.value();
    }

    public void enableTransparencyFixCauldrons(boolean value) {
        enableTransparencyFixCauldrons.set(value);
    }

    public void subscribeToEnableTransparencyFixCauldrons(Consumer<java.lang.Boolean> subscriber) {
        enableTransparencyFixCauldrons.observe(subscriber);
    }

    public boolean enableGrayScalingOfEntities() {
        return enableGrayScalingOfEntities.value();
    }

    public void enableGrayScalingOfEntities(boolean value) {
        enableGrayScalingOfEntities.set(value);
    }

    public final DyeingControls_ dyeingControls = new DyeingControls_();
    public class DyeingControls_ implements DyeingControls {
        public boolean useSeparateKeybinding() {
            return dyeingControls_useSeparateKeybinding.value();
        }

        public void useSeparateKeybinding(boolean value) {
            dyeingControls_useSeparateKeybinding.set(value);
        }

        public void subscribeToUseSeparateKeybinding(Consumer<java.lang.Boolean> subscriber) {
            dyeingControls_useSeparateKeybinding.observe(subscriber);
        }

        public boolean useToggleMode() {
            return dyeingControls_useToggleMode.value();
        }

        public void useToggleMode(boolean value) {
            dyeingControls_useToggleMode.set(value);
        }

        public void subscribeToUseToggleMode(Consumer<java.lang.Boolean> subscriber) {
            dyeingControls_useToggleMode.observe(subscriber);
        }

        public boolean alwaysOnByDefault() {
            return dyeingControls_alwaysOnByDefault.value();
        }

        public void alwaysOnByDefault(boolean value) {
            dyeingControls_alwaysOnByDefault.set(value);
        }

        public void subscribeToAlwaysOnByDefault(Consumer<java.lang.Boolean> subscriber) {
            dyeingControls_alwaysOnByDefault.observe(subscriber);
        }

    }
    public java.util.List<java.lang.String> gelatinBlackListModid() {
        return gelatinBlackListModid.value();
    }

    public void gelatinBlackListModid(java.util.List<java.lang.String> value) {
        gelatinBlackListModid.set(value);
    }

    public void subscribeToGelatinBlackListModid(Consumer<java.util.List<java.lang.String>> subscriber) {
        gelatinBlackListModid.observe(subscriber);
    }

    public interface DyeingControls {
        boolean useSeparateKeybinding();
        void useSeparateKeybinding(boolean value);
        boolean useToggleMode();
        void useToggleMode(boolean value);
        boolean alwaysOnByDefault();
        void alwaysOnByDefault(boolean value);
    }
    public static class Keys {
        public final Option.Key enableDyeingEntities = new Option.Key("enableDyeingEntities");
        public final Option.Key enableDyeingPlayers = new Option.Key("enableDyeingPlayers");
        public final Option.Key enableDyeingBlocks = new Option.Key("enableDyeingBlocks");
        public final Option.Key enableTransparencyFixCauldrons = new Option.Key("enableTransparencyFixCauldrons");
        public final Option.Key enableGrayScalingOfEntities = new Option.Key("enableGrayScalingOfEntities");
        public final Option.Key dyeingControls_useSeparateKeybinding = new Option.Key("dyeingControls.useSeparateKeybinding");
        public final Option.Key dyeingControls_useToggleMode = new Option.Key("dyeingControls.useToggleMode");
        public final Option.Key dyeingControls_alwaysOnByDefault = new Option.Key("dyeingControls.alwaysOnByDefault");
        public final Option.Key gelatinBlackListModid = new Option.Key("gelatinBlackListModid");
    }
}

