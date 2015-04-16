package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.query.PropertyQuery;

import java.io.Serializable;
import java.util.List;
import java.util.Observable;

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

    @Override
    public <T> PropertyQuery<T> createQuery(Class<T> beanClass) {
        return new PropertyQuery<>(beanClass, this);
    }

    @Override
    public <T> T findById(Class<T> beanClass, String id) {
        Object bean = beanRepository.findBeanByDolphinId(id);
        if(bean != null && beanClass.isAssignableFrom(bean.getClass())) {
            return (T) bean;
        }
        return null;
    }

    public String getId(Object bean) {
        String id = beanRepository.getDolphinId(bean);
        if(id == null) {
            throw new RuntimeException("Given bean is not managed and has no id");
        }
        return id;
    }
}
