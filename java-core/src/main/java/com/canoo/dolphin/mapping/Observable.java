package com.canoo.dolphin.mapping;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public interface Observable<T> {

    void addValueListener(ValueChangeListener<Observable<T>, T> listener);

    void removeValueListener(ValueChangeListener<Observable<T>, T> listener);

    T getValue();

    void setValue(T value);
}
