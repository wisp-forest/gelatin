package io.wispforest.gelatin.common.compat;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Abstraction for interacting the Gelatin's Config if using single modules
 */
public abstract class GelatinConfigHelper {

    public static GelatinConfigHelper INSTANCE = null;

    public abstract void toggleEntityDyeing(boolean value);
    public abstract boolean isEntityDyeingEnabled();

    public abstract void togglePlayerDyeing(boolean value);
    public abstract boolean isPlayerDyeingEnabled();

    public abstract void toggleBlockDyeing(boolean value);
    public abstract boolean isBlockDyeingEnabled();

    //Client

    public abstract void addCauldronFixSubscriber(Consumer<Boolean> consumer);

    public abstract void toggleCauldronFix(boolean value);
    public abstract boolean isCauldronFixEnabled();

    public abstract void toggleGrayScalingOfEntity(boolean value);
    public abstract boolean isGrayScalingOfEntityEnabled();

    public abstract void toggleGrayScalingOfRainbowEntity(boolean value);
    public abstract boolean isGrayScalingOfRainbowEntityEnabled();

    public abstract void addGelatinBlacklistSubscriber(Consumer<List<String>> consumer);

    public abstract void addToGelatinBlacklist(String value);
    public abstract void addToGelatinBlacklist(Collection<String> values);

    public abstract List<String> getGelatinBlacklist();
}
