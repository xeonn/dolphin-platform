package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.internal.BeanBuilder;

/**
 * Created by hendrikebbers on 09.05.16.
 */
public interface ServerBeanBuilder extends BeanBuilder {

   <T> T createRootModel(Class<T> beanClass);
}
