package com.canoo.dolphin.collections;

import com.canoo.dolphin.event.Subscription;

import java.util.Collection;
import java.util.List;

public interface ObservableList<E> extends List<E> {

    /**
     * Clears the ObservableList and add all elements from the collection.
     * @param col the collection with elements that will be added to this observableArrayList
     * @return true (as specified by Collection.add(E))
     * @throws NullPointerException if the specified collection contains one or more null elements
     */
    public boolean setAll(Collection<? extends E> col);

    Subscription onChanged(ListChangeListener<? super E> listener);
}
