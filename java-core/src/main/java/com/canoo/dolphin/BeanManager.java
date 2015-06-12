package com.canoo.dolphin;

import com.canoo.dolphin.event.BeanAddedListener;
import com.canoo.dolphin.event.BeanRemovedListener;
import com.canoo.dolphin.event.Subscription;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Basic Manager to work with beans that are synced with the client.
 */
public interface BeanManager extends Serializable {

    /**
     * Checks if the given object is a dolphin bean that is synced with the client
     *
     * @param bean the object
     * @return true if the given object is a dolphin bean
     */
    boolean isManaged(Object bean);

    /**
     * Creates a new instance of the given dolphin bean class that will automatically be synced with the client.
     * The given class must be defined as a dolphin bean
     *
     * @param beanClass the bean class
     * @param <T>       bean type
     * @return the new bean instance
     */
    <T> T create(Class<T> beanClass);

    /**
     * Remove the given managed dolphin bean. by calling this method the given bean will become unmanaged and won't be
     * synced with the client.
     *
     * @param bean the bean
     */
    void remove(Object bean);

    /**
     * Remove all beans of the given type.
     *
     * @param beanClass the class that defines the bean type.
     */
    void removeAll(Class<?> beanClass);

    /**
     * Remove all given beans.
     *
     * @param beans the beans that should be removed.
     */
    void removeAll(Object... beans);

    /**
     * Remove all beans of the given type.
     *
     * @param beans the beans that should be removed.
     */
    void removeAll(Collection<?> beans);

    /**
     * Returns a list of all dolphin managed beans of the given type / class
     *
     * @param beanClass the bean type
     * @param <T>       the bean type
     * @return a list of all managed beans of the type
     */
    <T> List<T> findAll(Class<T> beanClass);

    /**
     * Subscribe a listener to all bean creation events for a specific class.
     *
     * @param beanClass the class for which creation events should be received
     * @param listener the listener which receives the creation-events
     * @param <T> the bean type
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    <T> Subscription onAdded(Class<T> beanClass, BeanAddedListener<T> listener);

    /**
     * Subscribe a listener to all bean creation events.
     *
     * @param listener the listener which receives the creation events
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    Subscription onAdded(BeanAddedListener<?> listener);

    /**
     * Subscribe a listener to all bean destruction events for a specific class.
     *
     * @param beanClass the class for which destruction events should be received
     * @param listener the listener which receives the destruction events
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    <T> Subscription onRemoved(Class<T> beanClass, BeanRemovedListener<T> listener);

    /**
     * Subscribe a listener to all bean destruction events.
     *
     * @param listener the listener which receives the destruction events
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    Subscription onRemoved(BeanRemovedListener<?> listener);

}
