package com.canoo.dolphin.collections;

import java.util.List;

public interface ListChangeEvent<E> {

    ObservableList<E> getSource();

    List<Change<E>> getChanges();

    interface Change<S> {

        int getFrom();

        int getTo();

        List<S> getRemovedElements();

        boolean isAdded();

        boolean isRemoved();

        boolean isReplaced();
    }
}
