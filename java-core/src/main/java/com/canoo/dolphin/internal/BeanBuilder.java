package com.canoo.dolphin.internal;

/**
 * Created by hendrikebbers on 25.09.15.
 */
public interface BeanBuilder {

    <T> T create(Class<T> beanClass);

}
