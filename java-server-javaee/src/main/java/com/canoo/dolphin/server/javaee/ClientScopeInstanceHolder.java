package com.canoo.dolphin.server.javaee;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

/**
 * Created by hendrikebbers on 01.03.16.
 */
public class ClientScopeInstanceHolder<T> {

    private Bean<T> bean;

    private CreationalContext<T> creationalContext;

    private T instance;

    public ClientScopeInstanceHolder(Bean<T> bean, CreationalContext<T> creationalContext, T instance) {
        this.bean = bean;
        this.creationalContext = creationalContext;
        this.instance = instance;
    }

    public Bean<T> getBean() {
        return bean;
    }

    public CreationalContext<T> getCreationalContext() {
        return creationalContext;
    }

    public T getInstance() {
        return instance;
    }
}
