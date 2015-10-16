package com.canoo.dolphin.collections;

import com.canoo.dolphin.event.Subscription;

import java.util.Collection;
import java.util.List;

/**
 * This interface extends the default {@link List} interface and adds the possibility to observe the list.
 * By adding a {@link ListChangeListener} (see {@link #onChanged(ListChangeListener)}) all mutations of the list can be observed.
 * In addition this class provides some convenience methods.
 *
 * The {@link ObservableList} interface is part of teh Dolphin Platform model API. Since the lifecylce of models will be
 * managed by the Dolphin Platform the API don't provide a public implementation for this interface. When defining models
 * a developer only need to use the interface like it is already descriped in the {@link com.canoo.dolphin.mapping.Property}
 * class.
 * Like the {@link com.canoo.dolphin.mapping.Property} a {@link ObservableList} can be used in any Dolphin Platform model
 * (see {@link com.canoo.dolphin.mapping.DolphinBean}).
 *
 * Example:
 *
 * <blockquote>
 * <pre>
 *     {@literal @}DolphinBean
 *     public class MyModel {
 *
 *         {@code private ObservableList<String> values;}
 *
 *         {@code public ObservableList<String> getValues() {
 *              return values;
 *          }
 *         }
 *     }
 * </pre>
 * </blockquote>
 *
 * @param <E> type of elements in the list
 */
public interface ObservableList<E> extends List<E> {

    /**
     * Clears the ObservableList and add all elements from the collection.
     * @param col the collection with elements that will be added to this observableArrayList
     * @return true (as specified by Collection.add(E))
     * @throws NullPointerException if the specified collection contains one or more null elements
     */
    boolean setAll(Collection<? extends E> col);

    /**
     * Adds a change lister to the list that will be fired whenever the content of the list changes. This
     * will happen if an element is added or removed to the list, for example.
     * @param listener The listener that will be registered
     * @return a {@link Subscription} instance that can be used to deregister the listener.
     */
    Subscription onChanged(ListChangeListener<? super E> listener);
}
