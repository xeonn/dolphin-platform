package com.canoo.dolphin.mapping.impl;

import com.canoo.dolphin.mapping.Observable;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.mapping.ValueChangeListener;
import org.opendolphin.core.Dolphin;

/**
 * Created by hendrikebbers on 23.03.15.
 */
public class PropertyImpl<T> implements Property<T> {

    private AttributeValueObservable<T> valueObservable;

    private Dolphin dolphin;

    private String attributeId;

    public PropertyImpl(Dolphin dolphin, String attributeId) {
        this.dolphin = dolphin;
        this.attributeId = attributeId;
        valueObservable = new AttributeValueObservable<T>(dolphin, attributeId);
    }

    public void set(T value) {
        valueObservable.setValue(value);
    }

    public T get() {
        return valueObservable.getValue();
    }

    @Override
    public void addValueListener(ValueChangeListener<? super T> listener) {
        valueObservable.addValueListener(listener);
    }
    @Override
    public void removeValueListener(ValueChangeListener<? super T> listener) {
        valueObservable.removeValueListener(listener);
    }

}
