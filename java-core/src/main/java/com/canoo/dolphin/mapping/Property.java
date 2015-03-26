package com.canoo.dolphin.mapping;

public interface Property<T> {

    void set(T value);

    T get();

    void addValueListener(ValueChangeListener<? super T> listener);

    void removeValueListener(ValueChangeListener<? super T> listener);
}
