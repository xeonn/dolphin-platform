package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.util.Assert;

/**
 * Created by hendrikebbers on 14.07.16.
 */
public class BeanUtils {

    public static <T> T checkBean(T bean) {
        Assert.requireNonNull(bean, "bean");
        checkClass(bean.getClass());
        return bean;
    }

    public static <T> Class<T> checkClass(Class<T> beanClass) {
        Assert.requireNonNull(beanClass, "beanClass");
        DolphinBean annotation = beanClass.getAnnotation(DolphinBean.class);
        if (annotation == null) {
            throw new BeanDefinitionException(beanClass);
        }
        return beanClass;
    }
}
