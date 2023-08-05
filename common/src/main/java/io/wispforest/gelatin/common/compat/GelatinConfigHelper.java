package io.wispforest.gelatin.common.compat;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Abstraction for interacting the Gelatin's Config if using single modules
 */
public abstract class GelatinConfigHelper {

    public static GelatinConfigHelper INSTANCE = null;

    public abstract void entityDyeing(boolean value);
    public abstract boolean entityDyeing();

    public abstract void playerDyeing(boolean value);
    public abstract boolean playerDyeing();

    public abstract void blockDyeing(boolean value);
    public abstract boolean blockDyeing();

    //Client

    public abstract void observeCauldronFix(Consumer<Boolean> consumer);

    public abstract void cauldronFix(boolean value);
    public abstract boolean cauldronFix();

    public abstract void grayScalingOfEntity(boolean value);
    public abstract boolean grayScalingOfEntity();

    public abstract void dyeingControls_observeSeparateKeybinding(Consumer<Boolean> consumer);

    public abstract void dyeingControls_useSeparateKeybinding(boolean value);
    public abstract boolean dyeingControls_useSeparateKeybinding();

    public abstract void dyeingControls_observeToggleMode(Consumer<Boolean> consumer);

    public abstract void dyeingControls_useToggleMode(boolean value);
    public abstract boolean dyeingControls_useToggleMode();

    public abstract void dyeingControls_observeAlwaysOnByDefault(Consumer<Boolean> consumer);

    public abstract void dyeingControls_alwaysOnByDefault(boolean value);
    public abstract boolean dyeingControls_alwaysOnByDefault();

    public abstract void observe_GelatinBlacklist(Consumer<List<String>> consumer);

    public abstract void addToGelatinBlacklist(String value);
    public abstract void addToGelatinBlacklist(Collection<String> values);

    public abstract void removeFromGelatinBlacklist(String value);
    public abstract void removeFromGelatinBlacklist(Collection<String> values);

    public abstract List<String> getGelatinBlacklist();
}
