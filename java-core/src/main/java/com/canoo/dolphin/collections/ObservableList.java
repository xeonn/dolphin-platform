package com.canoo.dolphin.collections;

import java.util.List;

public interface ObservableList<E> extends List<E> {

    void addListListener(ListChangeListener<? super E> listener);

    void removeListListener(ListChangeListener<? super E> listener);
}
