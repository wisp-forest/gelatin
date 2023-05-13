package io.wispforest.gelatin.common.compat;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GelatinConfig extends ConfigWrapper<io.wispforest.gelatin.common.compat.GelatinConfigModel> {

    private final Option<java.lang.Boolean> enableDyeingEntities = this.optionForKey(new Option.Key("enableDyeingEntities"));
    private final Option<java.lang.Boolean> enableDyeingPlayers = this.optionForKey(new Option.Key("enableDyeingPlayers"));
    private final Option<java.lang.Boolean> enableDyeingBlocks = this.optionForKey(new Option.Key("enableDyeingBlocks"));
    private final Option<java.lang.Boolean> enableTransparencyFixCauldrons = this.optionForKey(new Option.Key("enableTransparencyFixCauldrons"));
    private final Option<java.lang.Boolean> enableGrayScalingOfEntities = this.optionForKey(new Option.Key("enableGrayScalingOfEntities"));
    private final Option<java.lang.Boolean> enableGrayScaleRainbowEntities = this.optionForKey(new Option.Key("enableGrayScaleRainbowEntities"));
    private final Option<java.util.List<java.lang.String>> gelatinBlackListModid = this.optionForKey(new Option.Key("gelatinBlackListModid"));

    private GelatinConfig() {
        super(io.wispforest.gelatin.common.compat.GelatinConfigModel.class);
    }

    public static GelatinConfig createAndLoad() {
        var wrapper = new GelatinConfig();
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

    public boolean enableGrayScaleRainbowEntities() {
        return enableGrayScaleRainbowEntities.value();
    }

    public void enableGrayScaleRainbowEntities(boolean value) {
        enableGrayScaleRainbowEntities.set(value);
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




}

