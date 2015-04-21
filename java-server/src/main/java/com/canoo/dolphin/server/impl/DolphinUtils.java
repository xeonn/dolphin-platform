package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.DolphinProperty;
import com.canoo.dolphin.mapping.Property;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public class DolphinUtils {

    public static <T> void forAllProperties(Class<T> beanClass, FieldIterator fieldIterator) {
        for (Field field : ReflectionHelper.getInheritedDeclaredFields(beanClass)) {
            if (Property.class.isAssignableFrom(field.getType())) {
                String attributeName = getDolphinAttributePropertyNameForField(field);
                fieldIterator.run(field, attributeName);
            }
        }
    }

    public static BeanInfo getBeanInfo(Class<?> modelClass) {
        return validate(getBeanInfo(new BetterBeanInfo(), Collections.<Class<?>>singleton(modelClass)));
    }

    private static BeanInfo validate(BeanInfo beanInfo) {
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            if (Collection.class.isAssignableFrom(descriptor.getPropertyType())) {
                if(descriptor.getWriteMethod() != null) {
                    throw new IllegalArgumentException("Collections should not be set, method: " + descriptor.getWriteMethod().getName());
                }
                if (!List.class.isAssignableFrom(descriptor.getPropertyType())) {
                    throw new IllegalArgumentException("Collections must be subtypes of List, method: " + descriptor.getName());
                }
            }
            validatePropertyName(descriptor);
        }
        return beanInfo;
    }

    public static <T> void forAllObservableLists(Class<T> beanClass, FieldIterator fieldIterator) {
        for (Field field : ReflectionHelper.getInheritedDeclaredFields(beanClass)) {
            if (ObservableList.class.isAssignableFrom(field.getType())) {

                String attributeName = getDolphinAttributePropertyNameForField(field);
                fieldIterator.run(field, attributeName);
            }
        }
    }

    public static String getDolphinAttributeName(PropertyDescriptor descriptor) {
        if(ReflectionHelper.isProperty(descriptor)){
            return descriptor.getName().substring(0, descriptor.getName().length() - "Property".length());
        }
        return descriptor.getName();
    }

    private static void validatePropertyName(PropertyDescriptor descriptor) {
        if(ReflectionHelper.isProperty(descriptor) && !descriptor.getName().endsWith("Property")) {
            throw new IllegalArgumentException(String.format("Getter for property %s should end with \"Property\"", descriptor.getName()));
        }
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
        return beanAnnotation == null || beanAnnotation.value().isEmpty()? beanClass.getName() : beanAnnotation.value();
    }

    static BeanInfo getBeanInfo(BetterBeanInfo betterBeanInfo, Set<Class<?>> modelClasses) {
        if(modelClasses.isEmpty()){
            return betterBeanInfo;
        }
        try {
            Set<Class<?>> superclasses = new HashSet<>();
            for (Class<?> modelClass : modelClasses) {
                BeanInfo beanInfo = Introspector.getBeanInfo(modelClass);
                for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                    if (!"class".equals(propertyDescriptor.getName())) {
                        betterBeanInfo.addPropertyDescriptors(propertyDescriptor);
                    }
                }
                superclasses.addAll(Arrays.asList(modelClass.getInterfaces()));
            }
            return getBeanInfo(betterBeanInfo, superclasses);
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public interface FieldIterator {
        public abstract void run(Field field, String attributeName);
    }

    public static <T> Property<T> getProperty(Object bean, String name) throws IllegalAccessException {
        return (Property<T>) (ReflectionHelper.isProxyInstance(bean) ? getPropertyForProxy(bean, name) : getPropertyForClass(bean, name));
    }

    private static <T> Property<T> getPropertyForClass(Object bean, String name) {
        for (Field field : ReflectionHelper.getInheritedDeclaredFields(bean.getClass())) {
            if (Property.class.isAssignableFrom(field.getType())) {
                if (name.equals(getDolphinAttributePropertyNameForField(field))) {
                    return (Property<T>) ReflectionHelper.getPrivileged(field, bean);
                }
            }
        }
        return null;
    }

    private static <T> Property<T> getPropertyForProxy(Object bean, String name) {
        return ((DolphinModelInvocationHander) Proxy.getInvocationHandler(bean)).getProperty(name);
    }

}
