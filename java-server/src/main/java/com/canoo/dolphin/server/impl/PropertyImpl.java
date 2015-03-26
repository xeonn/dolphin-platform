package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.mapping.ValueChangeEvent;
import com.canoo.dolphin.mapping.ValueChangeListener;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PropertyImpl<T> implements Property<T> {

    private final List<ValueChangeListener<? super T>> listeners = new CopyOnWriteArrayList<>();

    private final Dolphin dolphin;

    private final String attributeId;

    public PropertyImpl(Dolphin dolphin, String attributeId) {
        this.dolphin = dolphin;
        this.attributeId = attributeId;
        dolphin.findAttributeById(attributeId).addPropertyChangeListener(Attribute.VALUE, new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                set((T) evt.getNewValue());
            }
        });

    }

    public void set(T value) {
        if(value == null || DolphinUtils.isBasicType(value.getClass())) {
            doSet(null);
        } else {
            //TODO
        }
    }

    private void doSet(T newValue) {
        T oldValue = get();
        setValueInDolphin(newValue);
        firePropertyChanged(oldValue, newValue);
    }

    protected void setValueInDolphin(T value) {
        getAttribute().setValue(value);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) getAttribute().getValue();
    }

    protected Attribute getAttribute() {
        return dolphin.findAttributeById(attributeId);
    }

    public void addValueListener(ValueChangeListener<? super T> listener) {
        listeners.add(listener);
    }

    public void removeValueListener(ValueChangeListener<? super T> listener) {
        listeners.remove(listener);
    }

    protected void firePropertyChanged(T oldValue, T newValue) {
        for(ValueChangeListener<? super T> listener : listeners) {
            listener.valueChanged(new ValueChangeEvent<>(this,
                    oldValue, newValue));
        }
    }

}
