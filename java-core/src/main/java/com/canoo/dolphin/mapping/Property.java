package com.canoo.dolphin.mapping;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.event.ValueChangeListener;

/**
 * Defines a property that can be part of a model (see {@link DolphinBean}). Since Java has no native property system this is needed to provide
 * listener / observer support to properties.
 *
 * The public API of Dolphin Platform don't contain an implementation of this interface since the lifecycle of all
 * models must be managed by the {@link com.canoo.dolphin.BeanManager}. By using the {@link Property} interface a small
 * Dolphin Platform model will look like this:
 *
 * <blockquote>
 * <pre>
 *     {@literal @}DolphinBean
 *     public class MyModel {
 *
 *
 *         {@code private Property<String> value;}
 *
 *         {@code public Property<String> valueProperty() {
 *              return value;
 *          }
 *         }
 *     }
 * </pre>
 * </blockquote>
 *
 * The value can be easily accessed and modified by calling the {@link #set(Object)} and {@link #get()} method of the
 * {@link Property} but often the model classes provide some convenience methods like shown in the following example:
 * <blockquote>
 * <pre>
 *     {@literal @}DolphinBean
 *     public class MyModel {
 *
 *
 *         {@code private Property<String> value;}
 *
 *         {@code public Property<String> valueProperty() {
 *              return value;
 *          }
 *         }
 *
 *         public String getValue() {
 *              return value.get();
 *         }
 *
 *         public void setValue(String value) {
 *              this.value.set(value);
 *         }
 *     }
 * </pre>
 * </blockquote>
 *
 * Currently Dolphin Platform models support only the {@link Property} and {@link com.canoo.dolphin.collections.ObservableList}
 * interfaces to define attributes and collections in models. But by just using this 2 interfaces it's easy to create
 * hierarchical models because a {@link Property} can contain another bean, for example. The following class shows this
 * design by a simple example:
 *
 * <blockquote>
 * <pre>
 *     {@literal @}DolphinBean
 *     public class MainModel {
 *
 *
 *         {@code private Property<MyModel> innerModel;}
 *
 *         {@code public Property<MyModel> innerModelProperty() {
 *              return innerModel;
 *          }
 *         }
 *     }
 * </pre>
 * </blockquote>
 *
 * For more information see {@link DolphinBean}
 *
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
    Subscription onChanged(ValueChangeListener<? super T> listener);
}
