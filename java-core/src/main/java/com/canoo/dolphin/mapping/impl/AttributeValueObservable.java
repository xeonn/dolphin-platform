package com.canoo.dolphin.mapping.impl;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by hendrikebbers on 23.03.15.
 */
public class AttributeValueObservable<T> extends AbstractAttributeObservable<T> {

    protected AttributeValueObservable(Dolphin dolphin, String attributeId) {
        super(dolphin, attributeId);

        dolphin.findAttributeById(attributeId).addPropertyChangeListener(Attribute.VALUE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setValue((T)evt.getNewValue());
            }
        });
    }

    @Override
    public T getValue() {
        return (T) getAttribute().getValue();
    }

    @Override
    public void setValueInDolphin(T value) {
        getAttribute().setValue(value);
    }
}
