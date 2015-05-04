package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.event.BeanAddedListener;
import com.canoo.dolphin.event.BeanRemovedListener;
import com.canoo.dolphin.event.Subscription;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A {@code BeanManagerImpl} implements {@link BeanManager}
 */
// TODO Do we really need this or can we move the implementation to BeanManager directly?
public class BeanManagerImpl implements BeanManager {

    private final BeanRepository beanRepository;
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
        removeAll(Arrays.asList(beans));
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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Subscription onAdded(BeanAddedListener<Object> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <T>Subscription onRemoved(Class<T> beanClass, BeanRemovedListener<? super T> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Subscription onRemoved(BeanRemovedListener<Object> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
