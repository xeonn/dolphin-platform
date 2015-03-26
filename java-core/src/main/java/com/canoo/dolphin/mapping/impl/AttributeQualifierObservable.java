package com.canoo.dolphin.mapping.impl;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public class AttributeQualifierObservable extends AbstractAttributeObservable<String> {

    protected AttributeQualifierObservable(Dolphin dolphin, String attributeId) {
        super(dolphin, attributeId);

        dolphin.findAttributeById(attributeId).addPropertyChangeListener(Attribute.QUALIFIER_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setValue((String) evt.getNewValue());
            }
        });
    }

    @Override
    public String getValue() {
        return getAttribute().getQualifier();
    }

    @Override
    public void setValueInDolphin(String value) {
        throw new RuntimeException("Qualifier can't be set by hand");
    }
}
