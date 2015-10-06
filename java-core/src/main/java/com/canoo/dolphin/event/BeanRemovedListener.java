package com.canoo.dolphin.event;

/**
 * A listener that can be used to react on the destruction of beans.
 * @param <T> Bean type
 */
public interface BeanRemovedListener<T> {

    /**
     * Method will be called whenever a bean of type {@link T} was destructed.
     * @param bean the destructed bean.
     */
    void beanDestructed(T bean);

}
