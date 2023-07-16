package io.wispforest.gelatin.common.compat;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class GelatinDefaultConfig extends GelatinConfigHelper {

    public static GelatinDefaultConfig INSTANCE = new GelatinDefaultConfig();

    private boolean enableDyeingEntities = true;

    private boolean enableDyeingPlayers = true;

    private boolean enableDyeingBlocks = true;

    //---

    private List<Consumer<Boolean>> cauldronFix_subscribers = new ArrayList<>();

    private boolean enableTransparencyFixCauldrons = true;

    private boolean enableGrayScalingOfEntities = false;

    private boolean enableGrayScaleRainbowEntities = false;

    private List<Consumer<List<String>>> gelatinBlacklist_subscribers = new ArrayList<>();

    private List<String> gelatinBlackListModid = new ArrayList<>();

    //---

    @Override public void toggleEntityDyeing(boolean value) { this.enableDyeingEntities = value; }
    @Override public boolean isEntityDyeingEnabled() { return enableDyeingEntities; }

    @Override public void togglePlayerDyeing(boolean value) { this.enableDyeingPlayers = value; }
    @Override public boolean isPlayerDyeingEnabled() { return enableDyeingPlayers; }

    @Override public void toggleBlockDyeing(boolean value) { this.enableDyeingBlocks = value; }
    @Override public boolean isBlockDyeingEnabled() { return enableDyeingBlocks; }

    //---

    @Override public void addCauldronFixSubscriber(Consumer<Boolean> consumer) { this.cauldronFix_subscribers.add(consumer); }

    @Override public void toggleCauldronFix(boolean value) {
        this.enableTransparencyFixCauldrons = value;

        if(!this.cauldronFix_subscribers.isEmpty()){
            this.cauldronFix_subscribers.forEach(c -> c.accept(this.enableTransparencyFixCauldrons));
        }
    }

    @Override public boolean isCauldronFixEnabled() { return enableTransparencyFixCauldrons; }

    @Override public void toggleGrayScalingOfEntity(boolean value) { this.enableGrayScalingOfEntities = value; }
    @Override public boolean isGrayScalingOfEntityEnabled() { return enableGrayScalingOfEntities; }

    @Override public void toggleGrayScalingOfRainbowEntity(boolean value) { this.enableGrayScaleRainbowEntities = value; }

    @Override public boolean isGrayScalingOfRainbowEntityEnabled() { return enableGrayScaleRainbowEntities; }

    @Override public void addToGelatinBlacklist(String value) { this.gelatinBlackListModid.add(value); }
    @Override public void addToGelatinBlacklist(Collection<String> values) { this.gelatinBlackListModid.addAll(values); }

    @Override public void addGelatinBlacklistSubscriber(Consumer<List<String>> consumer) { this.gelatinBlacklist_subscribers.add(consumer); }

    @Override public List<String> getGelatinBlacklist() { return ImmutableList.copyOf(this.gelatinBlackListModid); }
}
