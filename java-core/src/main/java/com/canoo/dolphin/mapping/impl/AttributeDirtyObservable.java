package com.canoo.dolphin.mapping.impl;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public class AttributeDirtyObservable extends AbstractAttributeObservable<Boolean> {

    protected AttributeDirtyObservable(Dolphin dolphin, String attributeId) {
        super(dolphin, attributeId);

        dolphin.findAttributeById(attributeId).addPropertyChangeListener(Attribute.DIRTY_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setValue((Boolean) evt.getNewValue());
            }
        });
    }

    @Override
    public Boolean getValue() {
        return getAttribute().isDirty();
    }

    @Override
    public void setValueInDolphin(Boolean value) {
        throw new RuntimeException("Dirty State can't be set by hand");
    }
}