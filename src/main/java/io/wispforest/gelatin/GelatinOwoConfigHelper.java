package io.wispforest.gelatin;

import io.wispforest.gelatin.GelatinConfig;
import io.wispforest.gelatin.common.compat.GelatinConfigHelper;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class GelatinOwoConfigHelper extends GelatinConfigHelper {

    public static GelatinOwoConfigHelper INSTANCE = new GelatinOwoConfigHelper();

    private static GelatinConfig MAIN_CONFIG = null;

    public GelatinOwoConfigHelper(){}

    public static void init(){
        GelatinConfigHelper.INSTANCE = INSTANCE;
    }

    protected static GelatinConfig getConfig() {
        if(MAIN_CONFIG == null) MAIN_CONFIG = GelatinConfig.createAndLoad();

        return MAIN_CONFIG;
    }

    @Override public void toggleEntityDyeing(boolean value) { getConfig().enableDyeingEntities(value); }
    @Override public boolean isEntityDyeingEnabled() {
        return getConfig().enableDyeingEntities();
    }

    @Override public void togglePlayerDyeing(boolean value) { getConfig().enableDyeingPlayers(value); }
    @Override public boolean isPlayerDyeingEnabled() { return getConfig().enableDyeingPlayers(); }

    @Override public void toggleBlockDyeing(boolean value) { getConfig().enableDyeingBlocks(value); }
    @Override public boolean isBlockDyeingEnabled() { return getConfig().enableDyeingPlayers(); }

    @Override public void addCauldronFixSubscriber(Consumer<Boolean> consumer) { getConfig().subscribeToEnableTransparencyFixCauldrons(consumer); }

    @Override public void toggleCauldronFix(boolean value) { getConfig().enableTransparencyFixCauldrons(value); }
    @Override public boolean isCauldronFixEnabled() { return getConfig().enableTransparencyFixCauldrons(); }

    @Override public void toggleGrayScalingOfEntity(boolean value) { getConfig().enableGrayScalingOfEntities(value); }
    @Override public boolean isGrayScalingOfEntityEnabled() {
        return getConfig().enableGrayScalingOfEntities();
    }

    @Override public void toggleGrayScalingOfRainbowEntity(boolean value) { getConfig().enableGrayScalingOfRainbowEntities(value); }
    @Override public boolean isGrayScalingOfRainbowEntityEnabled() { return getConfig().enableGrayScalingOfRainbowEntities(); }

    @Override public void addGelatinBlacklistSubscriber(Consumer<List<String>> consumer) { getConfig().subscribeToGelatinBlackListModid(consumer);}

    @Override public void addToGelatinBlacklist(String value) {}
    @Override public void addToGelatinBlacklist(Collection<String> values) {}

    @Override public List<String> getGelatinBlacklist() {
        return getConfig().gelatinBlackListModid();
    }
}
