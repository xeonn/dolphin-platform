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

import com.canoo.dolphin.impl.converters.BooleanConverterFactory;
import com.canoo.dolphin.impl.converters.ByteConverterFactory;
import com.canoo.dolphin.impl.converters.DateConverterFactory;
import com.canoo.dolphin.impl.converters.DolphinBeanConverterFactory;
import com.canoo.dolphin.impl.converters.DoubleConverterFactory;
import com.canoo.dolphin.impl.converters.EnumConverterFactory;
import com.canoo.dolphin.impl.converters.FloatConverterFactory;
import com.canoo.dolphin.impl.converters.IntegerConverterFactory;
import com.canoo.dolphin.impl.converters.LongConverterFactory;
import com.canoo.dolphin.impl.converters.ShortConverterFactory;
import com.canoo.dolphin.impl.converters.StringConverterFactory;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.util.Assert;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

/**
 * The class {@code DolphinUtils} is a horrible class that we should get rid of asap.
 */
public class DolphinUtils {

    private DolphinUtils() {
    }

    public static String getDolphinAttributePropertyNameForField(Field propertyField) {
        return propertyField.getName();
    }

    public static String getDolphinPresentationModelTypeForClass(Class<?> beanClass) {
        return assertIsDolphinBean(beanClass).getName();
    }

    public static <T> T assertIsDolphinBean(T bean) {
        Assert.requireNonNull(bean, "bean");
        assertIsDolphinBean(bean.getClass());
        return bean;
    }

    public static <T> Class<T> assertIsDolphinBean(Class<T> beanClass) {
        if (!isDolphinBean(beanClass)) {
            throw new BeanDefinitionException(beanClass);
        }
        return beanClass;
    }

    public static boolean isDolphinBean(Class<?> beanClass) {
        Assert.requireNonNull(beanClass, "beanClass");
        return beanClass.isAnnotationPresent(DolphinBean.class);
    }
}
