package com.canoo.dolphin.server.query;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.impl.DolphinUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
        return with(name, new EqualsValueCheck<Object>(value));
    }

    public PropertyQuery<T> withNotEquals(String name, Object value) {
        return with(name, new NotEqualsValueCheck<Object>(value));
    }

    public PropertyQuery<T> withNull(String name) {
        return with(name, new NullValueCheck<Object>());
    }

    public PropertyQuery<T> withNotNull(String name) {
        return with(name, new NotNullValueCheck<Object>());
    }

    public PropertyQuery<T> with(String name, ValueCheck check) {
        queryParams.add(new PropertyQueryParam<Object>(name, check));
        return this;
    }

    public List<T> run() {
        List<T> result = new CopyOnWriteArrayList<>(manager.findAll(beanClass));

        for(T bean : result) {
            for(PropertyQueryParam queryParam : queryParams) {
                try {
                    Object currentValue = DolphinUtils.getProperty(bean, queryParam.getName()).get();
                    if (!queryParam.getCheck().check(currentValue)) {
                        result.remove(bean);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Can't execute query", e);
                }
            }
        }
        return result;
    }
}