/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
