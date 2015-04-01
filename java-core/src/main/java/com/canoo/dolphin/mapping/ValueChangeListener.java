package com.canoo.dolphin.mapping;

import java.beans.PropertyChangeEvent;
import java.util.EventListener;

/**
 * A change listener that supports generics
 * @param <T> type of the value that is observed by this listener
 */
public interface ValueChangeListener<T> extends EventListener {

    /**
     * This method is called whenever the observed value has been changed.
     * @param evt the event that defines the change of the observed value.
     */
    void valueChanged(ValueChangeEvent<? extends T> evt);

}
