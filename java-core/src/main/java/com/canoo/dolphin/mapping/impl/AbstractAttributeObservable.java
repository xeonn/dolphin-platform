package com.canoo.dolphin.mapping.impl;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;

/**
 * Created by hendrikebbers on 23.03.15.
 */
public abstract class AbstractAttributeObservable<T> extends AbstractObservable<T> {

    private Dolphin dolphin;

    private String attributeId;

    protected AbstractAttributeObservable(Dolphin dolphin, String attributeId) {
        this.dolphin = dolphin;
        this.attributeId = attributeId;
    }

    @Override
    public final void setValue(T value) {
        T oldValue = getValue();
        setValueInDolphin(value);
        firePropertyChanged(oldValue, value);
    }

    protected abstract void setValueInDolphin(T value);

    protected Attribute getAttribute() {
        return dolphin.findAttributeById(attributeId);
    }
}
