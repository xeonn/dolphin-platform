package com.canoo.dolphin;

import com.canoo.dolphin.event.BeanAddedListener;
import com.canoo.dolphin.event.BeanRemovedListener;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.impl.BeanBuilder;
import com.canoo.dolphin.impl.BeanRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Basic Manager to work with beans that are synced with the client.
 */
public class BeanManager implements Serializable {

    private final BeanRepository beanRepository;
    private final BeanBuilder beanBuilder;

    public BeanManager(BeanRepository beanRepository, BeanBuilder beanBuilder) {
        this.beanRepository = beanRepository;
        this.beanBuilder = beanBuilder;
    }

    /**
     * Checks if the given object is a dolphin bean that is synced with the client
     *
     * @param bean the object
     * @return true if the given object is a dolphin bean
     */
    public boolean isManaged(Object bean) {
        return beanRepository.isManaged(bean);
    }

    /**
     * Creates a new instance of the given dolphin bean class that will automatically be synced with the client.
     * The given class must be defined as a dolphin bean
     *
     * @param beanClass the bean class
     * @param <T>       bean type
     * @return the new bean instance
     */
    public <T> T create(Class<T> beanClass) {
        return beanBuilder.create(beanClass);
    }

    /**
     * Remove the given managed dolphin bean. by calling this method the given bean will become unmanaged and won't be
     * synced with the client.
     *
     * @param bean the bean
     */
    public void remove(Object bean) {
        beanRepository.delete(bean);
    }

    /**
     * Remove all beans of the given type.
     *
     * @param beanClass the class that defines the bean type.
     */
    public void removeAll(Class<?> beanClass) {
        for (Object bean : findAll(beanClass)) {
            beanRepository.delete(bean);
        }
    }

    /**
     * Remove all beans of the given type.
     *
     * @param beans the beans that should be removed.
     */
    public void removeAll(Object... beans) {
        for (final Object bean : beans) {
            remove(bean);
        }
    }

    /**
     * Remove all beans of the given type.
     *
     * @param beans the beans that should be removed.
     */
    public void removeAll(Collection<?> beans) {
        for (final Object bean : beans) {
            remove(bean);
        }
    }

    /**
     * Returns a list of all dolphin managed beans of the given type / class
     *
     * @param beanClass the bean type
     * @param <T>       the bean type
     * @return a list of all managed beans of the type
     */
    public <T> List<T> findAll(Class<T> beanClass) {
        return beanRepository.findAll(beanClass);
    }

    /**
     * Subscribe a listener to all bean creation events for a specific class.
     *
     * @param beanClass the class for which creation events should be received
     * @param listener the listener which receives the creation-events
     * @param <T> the bean type
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    public <T> Subscription onAdded(Class<T> beanClass, BeanAddedListener<? super T> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Subscribe a listener to all bean creation events.
     *
     * @param listener the listener which receives the creation events
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    public Subscription onAdded(BeanAddedListener<Object> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Subscribe a listener to all bean destruction events for a specific class.
     *
     * @param beanClass the class for which destruction events should be received
     * @param listener the listener which receives the destruction events
     * @param <T> the bean type
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    public <T>Subscription onRemoved(Class<T> beanClass, BeanRemovedListener<? super T> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Subscribe a listener to all bean destruction events.
     *
     * @param listener the listener which receives the destruction events
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    public Subscription onRemoved(BeanRemovedListener<Object> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
