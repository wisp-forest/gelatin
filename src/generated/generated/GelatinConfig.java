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
    private final Option<java.lang.Boolean> enableGrayScalingOfRainbowEntities = this.optionForKey(this.keys.enableGrayScalingOfRainbowEntities);
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

    public boolean enableGrayScalingOfRainbowEntities() {
        return enableGrayScalingOfRainbowEntities.value();
    }

    public void enableGrayScalingOfRainbowEntities(boolean value) {
        enableGrayScalingOfRainbowEntities.set(value);
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


    public static class Keys {
        public final Option.Key enableDyeingEntities = new Option.Key("enableDyeingEntities");
        public final Option.Key enableDyeingPlayers = new Option.Key("enableDyeingPlayers");
        public final Option.Key enableDyeingBlocks = new Option.Key("enableDyeingBlocks");
        public final Option.Key enableTransparencyFixCauldrons = new Option.Key("enableTransparencyFixCauldrons");
        public final Option.Key enableGrayScalingOfEntities = new Option.Key("enableGrayScalingOfEntities");
        public final Option.Key enableGrayScalingOfRainbowEntities = new Option.Key("enableGrayScalingOfRainbowEntities");
        public final Option.Key gelatinBlackListModid = new Option.Key("gelatinBlackListModid");
    }
}

