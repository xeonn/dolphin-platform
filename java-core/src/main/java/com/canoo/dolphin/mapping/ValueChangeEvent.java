package com.canoo.dolphin.mapping;

/**
 * Created by hendrikebbers on 23.03.15.
 */
public class ValueChangeEvent<S, T> {

    private S source;

    private T oldValue;

    private T newValue;

    public ValueChangeEvent(S source,
                               T oldValue, T newValue) {
        this.source = source;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public S getSource() {
        return source;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }
}
