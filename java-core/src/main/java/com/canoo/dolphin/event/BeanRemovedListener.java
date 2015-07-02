package com.canoo.dolphin.event;

public interface BeanRemovedListener<T> {

    void beanDestructed(T bean);

}
