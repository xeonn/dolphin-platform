package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.util.Assert;

/**
 * Created by hendrikebbers on 14.07.16.
 */
public class BeanUtils {

    public static void checkBean(Object bean) {
        try {
            Assert.requireNonNull(bean, "bean");
            checkClass(bean.getClass());
        } catch (Exception e) {
            throw new BeanDefinitionException("Object " + bean + " is not a valid Dolphin Platform bean!", e);
        }
    }

    public static void checkClass(Class<?> beanClass) {
        try {
            Assert.requireNonNull(beanClass, "beanClass");
            DolphinBean annotation = beanClass.getAnnotation(DolphinBean.class);
            if(annotation == null) {
                throw new BeanDefinitionException("The class " + beanClass + " is not annotated by " + DolphinBean.class);
            }
        } catch (Exception e) {
            throw new BeanDefinitionException("Class " + beanClass + " is not a valid Dolphin Platform bean class!", e);
        }
    }
}
