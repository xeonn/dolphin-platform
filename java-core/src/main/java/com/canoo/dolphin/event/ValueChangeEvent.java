package com.canoo.dolphin.event;

import com.canoo.dolphin.mapping.Property;

public class ValueChangeEvent<T> {

    private final Property<T> source;

    private final T oldValue;

    private final T newValue;

    public ValueChangeEvent(Property<T> source, T oldValue, T newValue) {
        this.source = source;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public Property<T> getSource() {
        return source;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }
}
