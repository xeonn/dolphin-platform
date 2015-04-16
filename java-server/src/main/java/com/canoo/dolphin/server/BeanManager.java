package com.canoo.dolphin.server;

import com.canoo.dolphin.server.query.PropertyQuery;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public interface BeanManager extends Serializable {

    boolean isManaged(Object bean);

    <T> T create(final Class<T> beanClass);

    <T> void delete(T bean);

    void deleteAll(Class<?> beanClass);

    <T> List<T> findAll(Class<T> beanClass);

    <T> PropertyQuery<T> createQuery(Class<T> beanClass);
}
