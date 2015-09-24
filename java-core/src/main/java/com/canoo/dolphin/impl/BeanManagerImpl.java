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

    @Override
    public boolean isManaged(Object bean) {
        return beanRepository.isManaged(bean);
    }

    @Override
    public <T> T create(Class<T> beanClass) {
        return beanBuilder.create(beanClass);
    }

    @Override
    public void remove(Object bean) {
        beanRepository.delete(bean);
    }

    @Override
    public void removeAll(Class<?> beanClass) {
        for (Object bean : findAll(beanClass)) {
            beanRepository.delete(bean);
        }
    }

    @Override
    public void removeAll(Object... beans) {
        for (final Object bean : beans) {
            remove(bean);
        }
    }

    @Override
    public void removeAll(Collection<?> beans) {
        for (final Object bean : beans) {
            remove(bean);
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> beanClass) {
        return beanRepository.findAll(beanClass);
    }

    @Override
    public <T> Subscription onAdded(Class<T> beanClass, BeanAddedListener<? super T> listener) {
        return beanRepository.addOnAddedListener(beanClass, listener);
    }

    @Override
    public Subscription onAdded(BeanAddedListener<Object> listener) {
        return beanRepository.addOnAddedListener(listener);
    }

    @Override
    public <T> Subscription onRemoved(Class<T> beanClass, BeanRemovedListener<? super T> listener) {
        return beanRepository.addOnRemovedListener(beanClass, listener);
    }

    @Override
    public Subscription onRemoved(BeanRemovedListener<Object> listener) {
        return beanRepository.addOnRemovedListener(listener);
    }

}
