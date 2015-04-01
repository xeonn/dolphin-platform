package com.canoo.dolphin.collections;

public interface ListChangeListener<E> {

    void listChanged(ListChangeEvent<? extends E> evt);

}
