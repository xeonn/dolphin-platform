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

    private final DolphinConverter dolphinConverter;

    private final String attributeId;

    private final Class<T> type;

    public PropertyImpl(DolphinConverter dolphinConverter, String attributeId, Class<T> type) {
        this.dolphinConverter = dolphinConverter;
        this.attributeId = attributeId;
        this.type = type;
        dolphinConverter.getDolphin().findAttributeById(attributeId).addPropertyChangeListener(Attribute.VALUE, new PropertyChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                set((T) evt.getNewValue());
            }
        });
    }

    public void set(T newValue) {
        T oldValue = get();
        getAttribute().setValue(dolphinConverter.convertToDolphinAttributeValue(type, newValue));
        firePropertyChanged(oldValue, newValue);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        final Object value = getAttribute().getValue();
        return (T) dolphinConverter.convertToPresentationModelProperty(type, value);
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    protected Attribute getAttribute() {
        return dolphinConverter.getDolphin().findAttributeById(attributeId);
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

    public interface DolphinConverter {
        Dolphin getDolphin();
        Object convertToDolphinAttributeValue(Class<?> clazz, Object object);
        Object convertToPresentationModelProperty(Class<?> clazz, Object object);
    }
}
