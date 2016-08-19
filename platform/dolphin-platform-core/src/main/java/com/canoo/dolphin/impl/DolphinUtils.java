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

import com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType;
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

    public static FieldType getFieldType(Class<?> clazz) {
        Assert.requireNonNull(clazz, "clazz");
        if (String.class.equals(clazz)) {
            return FieldType.STRING;
        }
        if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
            return FieldType.INT;
        }
        if (boolean.class.equals(clazz) || Boolean.class.equals(clazz)) {
            return FieldType.BOOLEAN;
        }
        if (long.class.equals(clazz) || Long.class.equals(clazz)) {
            return FieldType.LONG;
        }
        if (double.class.equals(clazz) || Double.class.equals(clazz)) {
            return FieldType.DOUBLE;
        }
        if (float.class.equals(clazz) || Float.class.equals(clazz)) {
            return FieldType.FLOAT;
        }
        if (byte.class.equals(clazz) || Byte.class.equals(clazz)) {
            return FieldType.BYTE;
        }
        if (short.class.equals(clazz) || Short.class.equals(clazz)) {
            return FieldType.SHORT;
        }
        if (Enum.class.isAssignableFrom(clazz)) {
            return FieldType.ENUM;
        }
        if (Date.class.isAssignableFrom(clazz) || Calendar.class.isAssignableFrom(clazz)) {
            return FieldType.DATE;
        }
        return FieldType.DOLPHIN_BEAN;
    }
}
