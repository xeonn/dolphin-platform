package com.canoo.dolphin.event;

import java.util.EventListener;

/**
 * A change listener that supports generics.
 * Usage in the Dolphin Platform:
 * Listener instances can be registered to {@link com.canoo.dolphin.mapping.Property}
 * instances to observe the internal value of the {@link com.canoo.dolphin.mapping.Property}. Whenever
 * the internal value of the {@link com.canoo.dolphin.mapping.Property} is changed the {@link #valueChanged(ValueChangeEvent)}
 * method will be called for all registered {@link ValueChangeListener} instances.
 * @param <T> type of the value that is observed by this listener
 */
public interface ValueChangeListener<T> extends EventListener {

    /**
     * This method is called whenever the observed value has been changed.
     * @param evt the event that defines the change of the observed value.
     */
    void valueChanged(ValueChangeEvent<? extends T> evt);

}
