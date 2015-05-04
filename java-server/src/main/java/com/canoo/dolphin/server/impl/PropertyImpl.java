package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.impl.info.PropertyInfo;
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


    public PropertyImpl(Attribute attribute, final PropertyInfo propertyInfo) {
        this.attribute = attribute;
        this.propertyInfo = propertyInfo;

        attribute.addPropertyChangeListener(Attribute.VALUE, new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                final T oldValue = (T) PropertyImpl.this.propertyInfo.convertFromDolphin(evt.getOldValue());
                final T newValue = (T) PropertyImpl.this.propertyInfo.convertFromDolphin(evt.getNewValue());
                firePropertyChanged(oldValue, newValue);
            }
        });
    }

    @Override
    public void set(T value) {
        attribute.setValue(propertyInfo.convertToDolphin(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) propertyInfo.convertFromDolphin(attribute.getValue());
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
        for(ValueChangeListener<? super T> listener : listeners) {
            listener.valueChanged(event);
        }
    }
}
