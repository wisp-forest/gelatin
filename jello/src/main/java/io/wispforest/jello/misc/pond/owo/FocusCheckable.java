package io.wispforest.jello.misc.pond.owo;

import io.wispforest.owo.util.EventSource;

public interface FocusCheckable {

    EventSource<FocusCheck> focusCheck();
}
