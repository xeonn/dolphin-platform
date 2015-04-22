package com.canoo.dolphin.event;

public interface BeanDestructionListener<T> {

    void beanDestructed(T model);

}
