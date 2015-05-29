package com.canoo.dolphin.server.query;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.DolphinUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PropertyQuery<T> {

    private final Class<T> beanClass;

    private final List<PropertyQueryParam<?>> queryParams;

    private final BeanManager manager;

    public PropertyQuery(Class<T> beanClass, BeanManager manager) {
        this.beanClass = beanClass;
        this.manager = manager;
        queryParams = new ArrayList<>();
    }

    public PropertyQuery<T> withEquals(String name, Object value) {
        return with(name, new EqualsValueCheck<>(value));
    }

    public PropertyQuery<T> withNotEquals(String name, Object value) {
        return with(name, new NotEqualsValueCheck<>(value));
    }

    public PropertyQuery<T> withNull(String name) {
        return with(name, new NullValueCheck<>());
    }

    public PropertyQuery<T> withNotNull(String name) {
        return with(name, new NotNullValueCheck<>());
    }

    public PropertyQuery<T> with(String name, ValueCheck check) {
        queryParams.add(new PropertyQueryParam<Object>(name, check));
        return this;
    }

    public T getSingle() {
        List<T> result = getAll();

        if(result.isEmpty()) {
            return null;
        }
        if(result.size() > 1) {
            throw new RuntimeException("Query returns multiple beans");
        }
        return result.get(0);
    }

    public List<T> getAll() {
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
