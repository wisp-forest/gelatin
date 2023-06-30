package io.wispforest.jello.misc.pond.owo;

import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.event.MouseUp;
import io.wispforest.owo.util.EventStream;

public interface FocusCheck {

    boolean allowFocusSource(Component.FocusSource source);

    static EventStream<FocusCheck> newStream() {
        return new EventStream<>(subscribers -> (source) -> {
            var anyTriggered = false;
            for (var subscriber : subscribers) {
                anyTriggered |= subscriber.allowFocusSource(source);
            }
            return anyTriggered;
        });
    }
}
