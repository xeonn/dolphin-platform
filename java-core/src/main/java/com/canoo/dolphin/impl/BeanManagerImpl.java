package com.canoo.dolphin.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.event.BeanAddedListener;
import com.canoo.dolphin.event.BeanRemovedListener;
import com.canoo.dolphin.event.Subscription;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class BeanManagerImpl implements Serializable, BeanManager {

    protected final BeanRepository beanRepository;
    private final BeanBuilder beanBuilder;

    public BeanManagerImpl(BeanRepository beanRepository, BeanBuilder beanBuilder) {
        this.beanRepository = beanRepository;
        this.beanBuilder = beanBuilder;
    }

    public boolean isManaged(Object bean) {
        return beanRepository.isManaged(bean);
    }

    public <T> T create(Class<T> beanClass) {
        return beanBuilder.create(beanClass);
    }

    public void remove(Object bean) {
        beanRepository.delete(bean);
    }

    public void removeAll(Class<?> beanClass) {
        for (Object bean : findAll(beanClass)) {
            beanRepository.delete(bean);
        }
    }

    public void removeAll(Object... beans) {
        for (final Object bean : beans) {
            remove(bean);
        }
    }

    public void removeAll(Collection<?> beans) {
        for (final Object bean : beans) {
            remove(bean);
        }
    }

    public <T> List<T> findAll(Class<T> beanClass) {
        return beanRepository.findAll(beanClass);
    }

    public <T> Subscription onAdded(Class<T> beanClass, BeanAddedListener<T> listener) {
        return beanRepository.addOnAddedListener(beanClass, listener);
    }

    public Subscription onAdded(BeanAddedListener<?> listener) {
        return beanRepository.addOnAddedListener(listener);
    }

    public <T> Subscription onRemoved(Class<T> beanClass, BeanRemovedListener<T> listener) {
        return beanRepository.addOnRemovedListener(beanClass, listener);
    }

    public Subscription onRemoved(BeanRemovedListener<?> listener) {
        return beanRepository.addOnRemovedListener(listener);
    }

}
