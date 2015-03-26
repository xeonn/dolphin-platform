package com.canoo.dolphin.mapping.impl;

import com.canoo.dolphin.mapping.ValueChangeListener;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public interface Observable<T> {

    void addValueListener(ValueChangeListener<? super T> listener);

    void removeValueListener(ValueChangeListener<? super T> listener);

    T getValue();

    void setValue(T value);
}
