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

    private AttributeBaseValueObservable<T> baseValueObservable;

    private AttributeDirtyObservable dirtyObservable;

    private AttributeQualifierObservable qualifierObservable;

    private AttributeTagObservable tagObservable;

    private Dolphin dolphin;

    private String attributeId;

    public PropertyImpl(Dolphin dolphin, String attributeId) {
        this.dolphin = dolphin;
        this.attributeId = attributeId;
        valueObservable = new AttributeValueObservable<T>(dolphin, attributeId);
        baseValueObservable = new AttributeBaseValueObservable<>(dolphin, attributeId);
        dirtyObservable = new AttributeDirtyObservable(dolphin, attributeId);
        qualifierObservable = new AttributeQualifierObservable(dolphin, attributeId);
        tagObservable = new AttributeTagObservable(dolphin, attributeId);
    }

    public void setValue(T value) {
        valueObservable.setValue(value);
    }

    public T getValue() {
        return valueObservable.getValue();
    }

    public void setBaseValue(T value) {
        baseValueObservable.setValue(value);
    }

    public T getBaseValue() {
        return baseValueObservable.getValue();
    }

    @Override
    public boolean isDirty() {
        return dirtyObservable.getValue();
    }

    @Override
    public String getQualifier() {
        return qualifierObservable.getValue();
    }

    @Override
    public String getTag() {
        return tagObservable.getValue();
    }

    @Override
    public void rebase() {
        dolphin.findAttributeById(attributeId).rebase();
    }

    @Override
    public void reset() {
        dolphin.findAttributeById(attributeId).reset();
    }

    @Override
    public void addDirtyListener(ValueChangeListener<Observable<Boolean>, Boolean> listener) {
        dirtyObservable.addValueListener(listener);
    }

    @Override
    public void removeDirtyListener(ValueChangeListener<Observable<Boolean>, Boolean> listener) {
        dirtyObservable.removeValueListener(listener);
    }

    @Override
    public void addValueListener(ValueChangeListener<Observable<T>, T> listener) {
        valueObservable.addValueListener(listener);
    }

    @Override
    public void addBaseValueListener(ValueChangeListener<Observable<T>, T> listener) {
        baseValueObservable.addValueListener(listener);
    }

    @Override
    public void removeValueListener(ValueChangeListener<Observable<T>, T> listener) {
        valueObservable.removeValueListener(listener);
    }

    @Override
    public void removeBaseValueListener(ValueChangeListener<Observable<T>, T> listener) {
        baseValueObservable.removeValueListener(listener);
    }

}
