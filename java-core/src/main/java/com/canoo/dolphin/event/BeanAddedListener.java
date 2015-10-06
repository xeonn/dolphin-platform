package com.canoo.dolphin.event;

/**
 * A listener that can be used to react on the creation of new beans.
 * @param <T> Bean type
 */
public interface BeanAddedListener<T> {

    /**
     * Method will be called whenever a new bean of type {@link T} was created.
     * @param bean the created bean.
     */
    void beanCreated(T bean);

}
