package com.canoo.dolphin.client.javafx.impl;

import com.canoo.dolphin.client.javafx.Converter;
import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ListChangeListener;

public class InternalListChangeListener<S, T> implements ListChangeListener<T> {

    private final javafx.collections.ObservableList<S> javaFXList;

    private final Converter<? super T, ? extends S> converter;

    private boolean onChange;

    public InternalListChangeListener(javafx.collections.ObservableList<S> javaFXList, Converter<? super T, ? extends S> converter) {
        this.converter = converter;
        this.javaFXList = javaFXList;
        onChange = false;
    }

    @Override
    public void listChanged(ListChangeEvent<? extends T> e) {
        onChange = true;
        try {
            for (ListChangeEvent.Change<? extends T> c : e.getChanges()) {
                if (c.isAdded()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        javaFXList.add(i, converter.convert(e.getSource().get(i)));
                    }
                } else if (c.isRemoved()) {
                    final int index = c.getFrom();
                    javaFXList.remove(index, index + c.getRemovedElements().size());
                }
            }
        } finally {
            onChange = false;
        }
    }

    public boolean isOnChange() {
        return onChange;
    }
}
