package com.canoo.dolphin.server.query;

import com.canoo.dolphin.server.BeanManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class PropertyQuery<T> {

    private Class<T> beanClass;

    private List<PropertyQueryParam<?>> queryParams;

    private BeanManager manager;

    public PropertyQuery(Class<T> beanClass, BeanManager manager) {
        this.beanClass = beanClass;
        this.manager = manager;
        queryParams = new ArrayList<>();
    }

    public PropertyQuery<T> withEquals(String name, Object value) {
        return withPropertyQueryParam(new PropertyQueryParam<Object>(name, new EqualsValueCheck<Object>(value)));
    }

    public PropertyQuery<T> withNotEquals(String name, Object value) {
        return withPropertyQueryParam(new PropertyQueryParam<Object>(name, new NotEqualsValueCheck<Object>(value)));
    }

    public PropertyQuery<T> withNull(String name) {
        return withPropertyQueryParam(new PropertyQueryParam<Object>(name, new NullValueCheck<Object>()));
    }

    public PropertyQuery<T> withNotNull(String name) {
        return withPropertyQueryParam(new PropertyQueryParam<Object>(name, new NotNullValueCheck<Object>()));
    }

    public PropertyQuery<T> withPropertyQueryParam(PropertyQueryParam<?> queryParam) {
        queryParams.add(queryParam);
        return this;
    }

    public List<T> run() {
        List<T> result = new ArrayList<>();

        List<T> all = manager.findAll(beanClass);
        for(T bean : all) {

        }
        return result;
    }
}
