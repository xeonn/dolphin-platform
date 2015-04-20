package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.server.BeanManager;

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
}
