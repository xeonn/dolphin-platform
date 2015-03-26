package com.canoo.dolphin.mapping.impl;

import com.canoo.dolphin.mapping.Observable;
import com.canoo.dolphin.mapping.ValueChangeEvent;
import com.canoo.dolphin.mapping.ValueChangeListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by hendrikebbers on 23.03.15.
 */
public abstract class AbstractObservable<T> implements Observable<T> {

    private List<ValueChangeListener<Observable<T>, T>> listeners;

    public AbstractObservable() {
        listeners = new CopyOnWriteArrayList<>();
    }

    public void addValueListener(ValueChangeListener<Observable<T>, T> listener) {
        listeners.add(listener);
    }

    public void removeValueListener(ValueChangeListener<Observable<T>, T> listener) {
        listeners.remove(listener);
    }

    protected void firePropertyChanged(T oldValue, T newValue) {
        for(ValueChangeListener<Observable<T>, T> listener : listeners) {
            listener.valueChanged(new ValueChangeEvent<Observable<T>, T>(this,
                    oldValue, newValue));
        }
    }

}
