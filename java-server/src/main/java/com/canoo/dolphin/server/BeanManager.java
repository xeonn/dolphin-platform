package com.canoo.dolphin.server;

import java.io.Serializable;
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
    <T> T create(final Class<T> beanClass);

    /**
     * Detaches the given managed dolphin bean. by calling this method the given bean will become unmanaged and won't be
     * synced with the client.
     *
     * @param bean the bean
     * @param <T>  type of the bean
     */
    <T> void detach(T bean);

    /**
     * Detaches all beans of the given type.
     *
     * @param beanClass the class that defines the bean type.
     */
    void detachAll(Class<?> beanClass);

    /**
     * Returns a list of all dolphin managed beans of the given type / class
     *
     * @param beanClass the bean type
     * @param <T>       the bean type
     * @return a list of all managed beans of the type
     */
    <T> List<T> findAll(Class<T> beanClass);

}
