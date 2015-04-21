package com.canoo.dolphin.event;

public interface BeanCreationListener<T> {

    void beanCreated(T model);

}
