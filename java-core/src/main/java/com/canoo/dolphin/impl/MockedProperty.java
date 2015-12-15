package com.canoo.dolphin.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.mapping.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class MockedProperty<T> implements Property<T> {

    private T value;

    private final List<ValueChangeListener<? super T>> listeners;

    public MockedProperty() {
        listeners = new ArrayList<>();
    }

    @Override
    public void set(final T value) {
        final T oldValue = this.value;
        this.value = value;
        for(ValueChangeListener<? super T> listener : listeners) {
            listener.valueChanged(new ValueChangeEvent<T>() {
                @Override
                public Property<T> getSource() {
                    return MockedProperty.this;
                }

                @Override
                public T getOldValue() {
                    return oldValue;
                }

                @Override
                public T getNewValue() {
                    return value;
                }
            });
        }
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public Subscription onChanged(final ValueChangeListener<? super T> listener) {
        listeners.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                listeners.remove(listener);
            }
        };
    }
}
