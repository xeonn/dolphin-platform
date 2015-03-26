package com.canoo.dolphin.mapping.impl;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public class DolphinUtils {

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

    public static boolean isBasicType(Class<?> cls) {
        if(cls.equals(String.class)) {
            return true;
        }
        if(cls.equals(Boolean.class)) {
            return true;
        }
        if(cls.equals(Boolean.TYPE)) {
            return true;
        }
        if(cls.equals(Integer.class)) {
            return true;
        }
        if(cls.equals(Integer.TYPE)) {
            return true;
        }
        if(cls.equals(Double.class)) {
            return true;
        }
        if(cls.equals(Double.TYPE)) {
            return true;
        }
        if(cls.equals(Float.class)) {
            return true;
        }
        if(cls.equals(Float.TYPE)) {
            return true;
        }
        if(cls.equals(Long.class)) {
            return true;
        }
        if(cls.equals(Long.TYPE)) {
            return true;
        }
        return false;
    }
}
