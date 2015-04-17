package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import org.opendolphin.core.Attribute;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PropertyImpl<T> implements Property<T> {

    private final List<ValueChangeListener<? super T>> listeners = new CopyOnWriteArrayList<>();

    private final BeanRepository beanRepository;

    private final Attribute attribute;

    public PropertyImpl(final BeanRepository beanRepository, final Attribute attribute) {
        this.beanRepository = beanRepository;
        this.attribute = attribute;


        attribute.addPropertyChangeListener(Attribute.VALUE, new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                final T oldValue = (T) PropertyImpl.this.beanRepository.mapDolphinToObjects(attribute, evt.getOldValue());
                final T newValue = (T) PropertyImpl.this.beanRepository.mapDolphinToObjects(attribute, evt.getNewValue());
                firePropertyChanged(oldValue, newValue);
            }
        });
    }

    public void set(T newValue) {
        if(Collection.class.isAssignableFrom(newValue.getClass())){
            throw new IllegalArgumentException("Type of the property must be a scalar, not a collection");
        }
        beanRepository.setValue(attribute, newValue);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) beanRepository.getValue(attribute);
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
}
