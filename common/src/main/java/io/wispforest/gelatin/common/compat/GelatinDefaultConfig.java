package io.wispforest.gelatin.common.compat;

import com.google.common.collect.ImmutableList;
import io.wispforest.owo.util.Observable;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GelatinDefaultConfig extends GelatinConfigHelper {

    public static GelatinDefaultConfig INSTANCE = new GelatinDefaultConfig();

    private boolean enableDyeingEntities = true;

    private boolean enableDyeingPlayers = true;

    private boolean enableDyeingBlocks = true;

    //---

    private final Observable<Boolean> enableTransparencyFixCauldrons = new Observable<>(true);

    private boolean enableGrayScalingOfEntities = false;

    //---

    private final Observable<Boolean> dyeingControl_useSeparateKeybinding = new Observable<>(false);
    private final Observable<Boolean> dyeingControl_useToggleMode = new Observable<>(false);
    private final Observable<Boolean> dyeingControl_alwaysOnByDefault = new Observable<>(false);

    //---

    private final Observable<List<String>> gelatinBlackListModid = new Observable<>(new ArrayList<>());

    //---

    @Override public void entityDyeing(boolean value) { this.enableDyeingEntities = value; }
    @Override public boolean entityDyeing() { return enableDyeingEntities; }

    @Override public void playerDyeing(boolean value) { this.enableDyeingPlayers = value; }
    @Override public boolean playerDyeing() { return enableDyeingPlayers; }

    @Override public void blockDyeing(boolean value) { this.enableDyeingBlocks = value; }
    @Override public boolean blockDyeing() { return enableDyeingBlocks; }

    //---

    @Override public void observeCauldronFix(Consumer<Boolean> consumer) { this.enableTransparencyFixCauldrons.observe(consumer); }

    @Override public void cauldronFix(boolean value) { this.enableTransparencyFixCauldrons.set(value); }
    @Override public boolean cauldronFix() { return enableTransparencyFixCauldrons.get(); }

    @Override public void grayScalingOfEntity(boolean value) { this.enableGrayScalingOfEntities = value; }
    @Override public boolean grayScalingOfEntity() { return enableGrayScalingOfEntities; }

    @Override public void dyeingControls_observeSeparateKeybinding(Consumer<Boolean> consumer) { this.dyeingControl_useSeparateKeybinding.observe(consumer); }

    @Override public void dyeingControls_useSeparateKeybinding(boolean value) { this.dyeingControl_useSeparateKeybinding.set(value); }
    @Override public boolean dyeingControls_useSeparateKeybinding() { return this.dyeingControl_useSeparateKeybinding.get(); }

    @Override public void dyeingControls_observeToggleMode(Consumer<Boolean> consumer) { this.dyeingControl_useToggleMode.observe(consumer); }

    @Override public void dyeingControls_useToggleMode(boolean value) { this.dyeingControl_useToggleMode.set(value); }
    @Override public boolean dyeingControls_useToggleMode() { return this.dyeingControl_useToggleMode.get(); }

    @Override public void dyeingControls_observeAlwaysOnByDefault(Consumer<Boolean> consumer) { this.dyeingControl_alwaysOnByDefault.observe(consumer); }

    @Override public void dyeingControls_alwaysOnByDefault(boolean value) { this.dyeingControl_alwaysOnByDefault.set(value); }
    @Override public boolean dyeingControls_alwaysOnByDefault() { return dyeingControl_alwaysOnByDefault.get(); }

    @Override public void addToGelatinBlacklist(String value) { this.addToGelatinBlacklist(List.of(value)); }
    @Override public void addToGelatinBlacklist(Collection<String> values) { this.modifyBlacklist(values, List::addAll); }

    @Override public void removeFromGelatinBlacklist(String value) { this.removeFromGelatinBlacklist(List.of(value)); }
    @Override public void removeFromGelatinBlacklist(Collection<String> values) { this.modifyBlacklist(values, List::removeAll); }

    @Override public void observe_GelatinBlacklist(Consumer<List<String>> consumer) { this.gelatinBlackListModid.observe(consumer); }

    @Override public List<String> getGelatinBlacklist() { return ImmutableList.copyOf(this.gelatinBlackListModid.get()); }

    private void modifyBlacklist(Collection<String> values, BiConsumer<List<String>, Collection<String>> consumer){
        List<String> currentList = gelatinBlackListModid.get();

        consumer.accept(currentList, values);

        gelatinBlackListModid.set(currentList);
    }

    // Based on owo Observable
    private static class Observable<T> implements Supplier<T> {
        private T value;
        private final List<Consumer<T>> observers;

        protected Observable(T initial){
            this.value = initial;
            this.observers = new ArrayList<>();
        }

        public void set(T newValue) {
            if (Objects.equals(this.value, newValue)) return;

            this.value = newValue;

            this.observers.forEach(observer -> observer.accept(value));
        }

        public void observe(Consumer<T> observer) {
            this.observers.add(observer);
        }

        @Override
        public T get() {
            return value;
        }
    }
}
