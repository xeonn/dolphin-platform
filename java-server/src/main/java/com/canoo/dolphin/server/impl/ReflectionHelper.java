package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.mapping.Property;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionHelper {


    public static Object getPrivileged(final Field field, final Object bean) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                boolean wasAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    return field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot set field: "
                            + field, ex);
                } finally {
                    field.setAccessible(wasAccessible);
                }
            }
        });
    }

    public static void setPrivileged(final Field field, final Object bean,
                                     final Object value) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                boolean wasAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    field.set(bean, value);
                    return null; // return nothing...
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot set field: "
                            + field, ex);
                } finally {
                    field.setAccessible(wasAccessible);
                }
            }
        });
    }

    public static List<Field> getInheritedDeclaredFields(Class<?> type) {
        List<Field> result = new ArrayList<>();
        Class<?> i = type;
        while (i != null && i != Object.class) {
            result.addAll(Arrays.asList(i.getDeclaredFields()));
            i = i.getSuperclass();
        }
        return result;
    }

    static boolean isProperty(PropertyDescriptor descriptor) {
        return Property.class.isAssignableFrom(descriptor.getPropertyType());
    }

    public static boolean isBasicType(Class<?> cls) {
        return cls.isPrimitive() || cls.equals(String.class) || cls.equals(Boolean.class) || cls.equals(Byte.class) || Number.class.isAssignableFrom(cls);
    }
}
