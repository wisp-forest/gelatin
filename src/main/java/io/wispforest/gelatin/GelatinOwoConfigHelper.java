package io.wispforest.gelatin;

import io.wispforest.gelatin.GelatinConfig;
import io.wispforest.gelatin.common.compat.GelatinConfigHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
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

    @Override public void entityDyeing(boolean value) { getConfig().enableDyeingEntities(value); }
    @Override public boolean entityDyeing() {
        return getConfig().enableDyeingEntities();
    }

    @Override public void playerDyeing(boolean value) { getConfig().enableDyeingPlayers(value); }
    @Override public boolean playerDyeing() { return getConfig().enableDyeingPlayers(); }

    @Override public void blockDyeing(boolean value) { getConfig().enableDyeingBlocks(value); }
    @Override public boolean blockDyeing() { return getConfig().enableDyeingPlayers(); }

    @Override public void observeCauldronFix(Consumer<Boolean> c) { getConfig().subscribeToEnableTransparencyFixCauldrons(c); }

    @Override public void cauldronFix(boolean value) { getConfig().enableTransparencyFixCauldrons(value); }
    @Override public boolean cauldronFix() { return getConfig().enableTransparencyFixCauldrons(); }

    @Override public void grayScalingOfEntity(boolean value) { getConfig().enableGrayScalingOfEntities(value); }
    @Override public boolean grayScalingOfEntity() {
        return getConfig().enableGrayScalingOfEntities();
    }

    @Override public void dyeingControls_observeSeparateKeybinding(Consumer<Boolean> c) { getConfig().dyeingControls.subscribeToUseSeparateKeybinding(c); }

    @Override public void dyeingControls_useSeparateKeybinding(boolean value) { getConfig().dyeingControls.useSeparateKeybinding(value); }
    @Override public boolean dyeingControls_useSeparateKeybinding() { return getConfig().dyeingControls.useSeparateKeybinding(); }

    @Override public void dyeingControls_observeToggleMode(Consumer<Boolean> c) {getConfig().dyeingControls.subscribeToUseToggleMode(c);}

    @Override public void dyeingControls_useToggleMode(boolean value) { getConfig().dyeingControls.useToggleMode(value); }
    @Override public boolean dyeingControls_useToggleMode() { return getConfig().dyeingControls.useToggleMode(); }

    @Override public void dyeingControls_observeAlwaysOnByDefault(Consumer<Boolean> consumer) { getConfig().dyeingControls.subscribeToAlwaysOnByDefault(consumer); }

    @Override public void dyeingControls_alwaysOnByDefault(boolean value) { getConfig().dyeingControls.alwaysOnByDefault(value); }
    @Override public boolean dyeingControls_alwaysOnByDefault() { return getConfig().dyeingControls.alwaysOnByDefault(); }

    @Override public void observe_GelatinBlacklist(Consumer<List<String>> c) { getConfig().subscribeToGelatinBlackListModid(c);}

    @Override public void addToGelatinBlacklist(String value) { addToGelatinBlacklist(List.of(value)); }
    @Override public void addToGelatinBlacklist(Collection<String> values) { modifyBlacklist(values, List::addAll); }

    @Override public void removeFromGelatinBlacklist(String value) { removeFromGelatinBlacklist(List.of(value)); }
    @Override public void removeFromGelatinBlacklist(Collection<String> values) { modifyBlacklist(values, List::removeAll); }

    @Override public List<String> getGelatinBlacklist() {
        return getConfig().gelatinBlackListModid();
    }

    private void modifyBlacklist(Collection<String> values, BiConsumer<List<String>, Collection<String>> consumer){
        List<String> currentList = new ArrayList<>(getGelatinBlacklist());

        consumer.accept(currentList, values);

        getConfig().gelatinBlackListModid(currentList);
    }
}
