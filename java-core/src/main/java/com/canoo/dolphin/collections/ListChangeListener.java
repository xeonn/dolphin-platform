package com.canoo.dolphin.collections;

/**
 * Listener that can be used to observe changes of an {@link ObservableList}.
 * @param <E> type of elements in the list
 */
public interface ListChangeListener<E> {

    /**
     * This method will be called whenever an {@link ObservableList} has changed.
     * @param evt defines the change.
     */
    void listChanged(ListChangeEvent<? extends E> evt);

}
