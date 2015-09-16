package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.DolphinProperty;
import com.canoo.dolphin.mapping.Property;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import static com.canoo.dolphin.impl.ClassRepository.FieldType.BASIC_TYPE;
import static com.canoo.dolphin.impl.ClassRepository.FieldType.DOLPHIN_BEAN;

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
        String attributeName = propertyField.getName();
        DolphinProperty propertyAnnotation = propertyField.getAnnotation(DolphinProperty.class);
        if (propertyAnnotation != null && !propertyAnnotation.value().isEmpty()) {
            attributeName = propertyAnnotation.value();
        }
        return attributeName;
    }

    public static String getDolphinPresentationModelTypeForClass(Class<?> beanClass) {
        final DolphinBean beanAnnotation = beanClass.getAnnotation(DolphinBean.class);
        return beanAnnotation == null || beanAnnotation.value().isEmpty() ? beanClass.getName() : beanAnnotation.value();
    }

    public static <T> Property<T> getProperty(Object bean, String name) throws IllegalAccessException {
        for (Field field : ReflectionHelper.getInheritedDeclaredFields(bean.getClass())) {
            if (Property.class.isAssignableFrom(field.getType()) && name.equals(getDolphinAttributePropertyNameForField(field))) {
                return (Property<T>) ReflectionHelper.getPrivileged(field, bean);
            }
        }
        return null;
    }

    public static Object mapFieldTypeToDolphin(ClassRepository.FieldType fieldType) {
        return fieldType.ordinal();
    }

    public static ClassRepository.FieldType mapFieldTypeFromDolphin(Object value) {
        try {
            return ClassRepository.FieldType.values()[(Integer) value];
        } catch (NullPointerException | ClassCastException | IndexOutOfBoundsException ex) {
            return ClassRepository.FieldType.UNKNOWN;
        }
    }

    public static ClassRepository.FieldType getFieldType(Object value) {
        if (value == null) {
            return ClassRepository.FieldType.UNKNOWN;
        }
        return ReflectionHelper.isBasicType(value.getClass()) ? BASIC_TYPE : DOLPHIN_BEAN;
    }

}
