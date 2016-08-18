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
package com.canoo.dolphin.reactive;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.util.Assert;
import rx.functions.Action1;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation for {@link TransformedProperty}
 * @param <T> type of the property
 */
public class TransformedPropertyImpl<T> implements TransformedProperty<T>, Action1<T> {

    private T value;

    private final Subscription subscription;

    private final List<ValueChangeListener<? super T>> listeners = new CopyOnWriteArrayList<>();

    public TransformedPropertyImpl(final Subscription subscription) {
        Assert.requireNonNull(subscription, "subscription");
        this.subscription = subscription;
    }

    @Override
    public void set(final T value) {
        throw new RuntimeException("The transformed property is bound to another property!");
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

    protected void firePropertyChanged(final T oldValue, final T newValue) {
        final ValueChangeEvent<T> event = new ValueChangeEvent<T>() {
            @Override
            public Property<T> getSource() {
                return TransformedPropertyImpl.this;
            }

            @Override
            public T getOldValue() {
                return oldValue;
            }

            @Override
            public T getNewValue() {
                return newValue;
            }
        };
        for(ValueChangeListener<? super T> listener : listeners) {
            listener.valueChanged(event);
        }
    }

    @Override
    public void unsubscribe() {
        subscription.unsubscribe();
    }

    @Override
    public void call(final T t) {
        T oldValue = this.value;
        this.value = t;
        firePropertyChanged(oldValue, value);
    }
}
