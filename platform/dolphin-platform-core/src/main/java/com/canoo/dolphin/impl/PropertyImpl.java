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

import com.canoo.dolphin.converter.ValueConverterException;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.internal.info.PropertyInfo;
import com.canoo.dolphin.mapping.MappingException;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.Attribute;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An implementation of {@link Property} that is used for all Dolphin Beans generated from class definitions.
 *
 * @param <T> The type of the wrapped property.
 */
public class PropertyImpl<T> implements Property<T> {

    private final Attribute attribute;
    private final PropertyInfo propertyInfo;
    private final List<ValueChangeListener<? super T>> listeners = new CopyOnWriteArrayList<>();


    public PropertyImpl(final Attribute attribute, final PropertyInfo propertyInfo) {
        this.attribute = Assert.requireNonNull(attribute, "attribute");
        this.propertyInfo = Assert.requireNonNull(propertyInfo, "propertyInfo");

        attribute.addPropertyChangeListener(Attribute.VALUE, new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                Assert.requireNonNull(evt, "evt");
                try {
                    final T oldValue = (T) PropertyImpl.this.propertyInfo.convertFromDolphin(evt.getOldValue());
                    final T newValue = (T) PropertyImpl.this.propertyInfo.convertFromDolphin(evt.getNewValue());
                    firePropertyChanged(oldValue, newValue);
                } catch (Exception e) {
                    throw new MappingException("Error in property change handling!", e);
                }
            }
        });
    }

    @Override
    public void set(T value) {
        try {
            attribute.setValue(propertyInfo.convertToDolphin(value));
        } catch (ValueConverterException e) {
            throw new MappingException("Error in mutating property value!", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        try {
        return (T) propertyInfo.convertFromDolphin(attribute.getValue());
        } catch (ValueConverterException e) {
            throw new MappingException("Error in accessing property value!", e);
        }
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
                return PropertyImpl.this;
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
        notifyInternalListeners(event);
        notifyExternalListeners(event);
    }

    protected void notifyExternalListeners(ValueChangeEvent<T> event) {
        for(ValueChangeListener<? super T> listener : listeners) {
            listener.valueChanged(event);
        }
    }

    protected void notifyInternalListeners(ValueChangeEvent<T> event) {
    }
}
