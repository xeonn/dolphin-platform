package com.canoo.dolphin.event;

import com.canoo.dolphin.mapping.Property;

/**
 * Defines a value changed event for a {@link Property}. A {@link Property} fires {@link ValueChangeEvent} to all
 * registered change listeners (see {@link ValueChangeListener}) for each change of internal value.
 * @param <T> Type of the {@link Property} that created this event.
 */
public interface ValueChangeEvent<T> {

    /**
     * The {@link Property} that fired this event.
     * @return the {@link Property} that fired this event.
     */
    Property<T> getSource();

    /**
     * Old internal value of the {@link Property} that fired this event.
     * @return Old internal value
     */
    T getOldValue();

    /**
     * New internal value of the {@link Property} that fired this event.
     * @return New internal value
     */
    T getNewValue();
}
