package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.event.BeanCreationListener;
import com.canoo.dolphin.event.BeanDestructionListener;
import com.canoo.dolphin.event.Subscription;

import java.util.List;

public class BeanManagerImpl implements BeanManager {

    private final BeanRepository beanRepository;

    public BeanManagerImpl(BeanRepository beanRepository) {
        this.beanRepository = beanRepository;
    }

    @Override
    public boolean isManaged(Object bean) {
        return beanRepository.isManaged(bean);
    }

    @Override
    public <T> T create(Class<T> beanClass) {
        return beanRepository.create(beanClass);
    }

    @Override
    public <T> void detach(T bean) {
        beanRepository.delete(bean);
    }

    @Override
    public void detachAll(Class<?> beanClass) {
        beanRepository.deleteAll(beanClass);
    }

    @Override
    public <T> List<T> findAll(Class<T> beanClass) {
        return beanRepository.findAll(beanClass);
    }

    @Override
    public <T> Subscription subscribeToBeanCreations(Class<T> beanClass, BeanCreationListener<? super T> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Subscription subscribeToBeanCreations(BeanCreationListener<?> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <T> Subscription subscribeToBeanDestructions(Class<T> beanClass, BeanDestructionListener<? super T> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Subscription subscribeToBeanCreations(BeanDestructionListener<?> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
