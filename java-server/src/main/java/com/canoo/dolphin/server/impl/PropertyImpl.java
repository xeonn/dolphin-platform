package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.mapping.ValueChangeEvent;
import com.canoo.dolphin.mapping.ValueChangeListener;
import org.opendolphin.core.Attribute;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PropertyImpl<T> implements Property<T> {

    private final List<ValueChangeListener<? super T>> listeners = new CopyOnWriteArrayList<>();

    private final BeanManagerAccess beanManagerAccess;

    private final Attribute attribute;

    public PropertyImpl(final BeanManagerAccess beanManagerAccess, final Attribute attribute) {
        this.beanManagerAccess = beanManagerAccess;
        this.attribute = attribute;


        attribute.addPropertyChangeListener(Attribute.VALUE, new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChanged((T) beanManagerAccess.map(attribute, evt.getOldValue()), (T) beanManagerAccess.map(attribute, evt.getNewValue()));
            }
        });
    }

    public void set(T newValue) {
        beanManagerAccess.setValue(attribute, newValue);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) beanManagerAccess.getValue(attribute);
    }

    public void addValueListener(ValueChangeListener<? super T> listener) {
        listeners.add(listener);
    }

    public void removeValueListener(ValueChangeListener<? super T> listener) {
        listeners.remove(listener);
    }

    protected void firePropertyChanged(T oldValue, T newValue) {
        final ValueChangeEvent<T> event = new ValueChangeEvent<>(this, oldValue, newValue);
        for(ValueChangeListener<? super T> listener : listeners) {
            listener.valueChanged(event);
        }
    }

    public interface BeanManagerAccess {

        Object getValue(Attribute attribute);

        void setValue(Attribute attribute, Object value);

        Object map(Attribute attribute, Object value);
    }

}
