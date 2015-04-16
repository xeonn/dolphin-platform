package com.canoo.dolphin.mapping;

/**
 * Defines a property that can be part of a model. Since Java has no native property system this is needed to provide
 * listener / observer support to properties.
 * @param <T> Type of the property must be a scalar, not a collection
 */
public interface Property<T> {

    /**
     * Sets the value of the property
     * @param value the new value
     */
    void set(T value);

    /**
     * Returns the value of the property
     * @return the current value
     */
    T get();

    /**
     * Adds a change listener to the property that will be called whenever the value of the property changes
     * @param listener the change listener
     */
    void addValueListener(ValueChangeListener<? super T> listener);

    /**
     * Removes a change listener
     * @param listener the change listener
     */
    void removeValueListener(ValueChangeListener<? super T> listener);

}
