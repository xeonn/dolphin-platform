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
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

/**
 * The class {@code DolphinUtils} is a horrible class that we should get rid of asap.
 */
public class DolphinUtils {

    private DolphinUtils() {
    }

    public static boolean isDolphinBean(Class cls) {
        return DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN.equals(DolphinUtils.getFieldType(cls));
    }


    public static String getDolphinAttributeName(PropertyDescriptor descriptor) {
        if (ReflectionHelper.isProperty(descriptor)) {
            return descriptor.getName().substring(0, descriptor.getName().length() - "Property".length());
        }
        return descriptor.getName();
    }

    public static String getDolphinAttributePropertyNameForField(Field propertyField) {
        return propertyField.getName();
    }

    public static String getDolphinPresentationModelTypeForClass(Class<?> beanClass) {
        return BeanUtils.checkClass(beanClass).getName();
    }

    public static <T> Property<T> getProperty(Object bean, String name) throws IllegalAccessException {
        for (Field field : ReflectionHelper.getInheritedDeclaredFields(bean.getClass())) {
            if (Property.class.isAssignableFrom(field.getType()) && name.equals(getDolphinAttributePropertyNameForField(field))) {
                return (Property<T>) ReflectionHelper.getPrivileged(field, bean);
            }
        }
        return null;
    }

    public static String getFieldType(Class<?> clazz) {
        Assert.requireNonNull(clazz, "clazz");
        if (String.class.equals(clazz)) {
            return StringConverterFactory.FIELD_TYPE_STRING;
        }
        if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
            return IntegerConverterFactory.FIELD_TYPE_INT;
        }
        if (boolean.class.equals(clazz) || Boolean.class.equals(clazz)) {
            return BooleanConverterFactory.FIELD_TYPE_BOOLEAN;
        }
        if (long.class.equals(clazz) || Long.class.equals(clazz)) {
            return LongConverterFactory.FIELD_TYPE_LONG;
        }
        if (double.class.equals(clazz) || Double.class.equals(clazz)) {
            return DoubleConverterFactory.FIELD_TYPE_DOUBLE;
        }
        if (float.class.equals(clazz) || Float.class.equals(clazz)) {
            return FloatConverterFactory.FIELD_TYPE_FLOAT;
        }
        if (byte.class.equals(clazz) || Byte.class.equals(clazz)) {
            return ByteConverterFactory.FIELD_TYPE_BYTE;
        }
        if (short.class.equals(clazz) || Short.class.equals(clazz)) {
            return ShortConverterFactory.FIELD_TYPE_SHORT;
        }
        if (Enum.class.isAssignableFrom(clazz)) {
            return EnumConverterFactory.FIELD_TYPE_ENUM;
        }
        if (Date.class.isAssignableFrom(clazz) || Calendar.class.isAssignableFrom(clazz)) {
            return DateConverterFactory.FIELD_TYPE_DATE;
        }
        return DolphinBeanConverterFactory.FIELD_TYPE_DOLPHIN_BEAN;
    }
}
