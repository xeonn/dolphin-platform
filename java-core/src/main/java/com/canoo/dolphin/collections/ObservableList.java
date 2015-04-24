package com.canoo.dolphin.collections;

import com.canoo.dolphin.event.Subscription;

import java.util.List;

public interface ObservableList<E> extends List<E> {

    Subscription onChanged(ListChangeListener<? super E> listener);
}
