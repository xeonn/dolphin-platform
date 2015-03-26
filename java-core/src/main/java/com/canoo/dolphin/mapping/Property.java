package com.canoo.dolphin.mapping;

import com.canoo.dolphin.mapping.impl.AbstractObservable;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public interface Property<T> {

    void setValue(T value);

    T getValue();

    void setBaseValue(T value);

    T getBaseValue();

    boolean isDirty();

    String getQualifier();

    String getTag();

    void rebase();

    void reset();

    void addDirtyListener(ValueChangeListener<Observable<Boolean>, Boolean> listener);

    void removeDirtyListener(ValueChangeListener<Observable<Boolean>, Boolean> listener);

    void addValueListener(ValueChangeListener<Observable<T>, T> listener);

    void addBaseValueListener(ValueChangeListener<Observable<T>, T> listener);

    void removeValueListener(ValueChangeListener<Observable<T>, T> listener);

    void removeBaseValueListener(ValueChangeListener<Observable<T>, T> listener);
}
