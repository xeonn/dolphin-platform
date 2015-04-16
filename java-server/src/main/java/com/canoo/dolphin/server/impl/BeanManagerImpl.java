package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.impl.BeanRepository;

import java.io.Serializable;
import java.util.List;

public class BeanManagerImpl implements BeanManager {

    private final BeanRepository beanRepository;

    public BeanManagerImpl(BeanRepository beanRepository) {
        this.beanRepository = beanRepository;
    }

    public boolean isManaged(Object bean) {
        return beanRepository.isManaged(bean);
    }

    public <T> T create(final Class<T> beanClass) {
        return beanRepository.create(beanClass);
    }

    public <T> void delete(T bean) {
        beanRepository.delete(bean);
    }

    public void deleteAll(Class<?> beanClass) {
        beanRepository.deleteAll(beanClass);
    }

    public <T> List<T> findAll(Class<T> beanClass) {
        return beanRepository.findAll(beanClass);
    }
}
