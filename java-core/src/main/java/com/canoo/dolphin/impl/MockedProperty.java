/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    @Override
    public String toString() {
        return "Dolphin " + MockedProperty.class.getSimpleName() + "[value: " + value + "]";
    }
}
