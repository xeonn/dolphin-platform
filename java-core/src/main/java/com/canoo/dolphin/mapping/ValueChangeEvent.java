package com.canoo.dolphin.mapping;

/**
 * Created by hendrikebbers on 23.03.15.
 */
public class ValueChangeEvent<T> {

    private Object source;

    private T oldValue;

    private T newValue;

    public ValueChangeEvent(Object source,
                               T oldValue, T newValue) {
        this.source = source;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public Object getSource() {
        return source;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }
}
