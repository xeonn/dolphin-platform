package com.canoo.dolphin.server.impl.info;

import com.canoo.dolphin.server.impl.Converters;
import com.canoo.dolphin.server.impl.ReflectionHelper;
import org.opendolphin.core.Attribute;

import java.lang.reflect.Field;

public class ClassPropertyInfo extends PropertyInfo {

    private final Field field;

    public ClassPropertyInfo(Attribute attribute, String attributeName, Converters.Converter converter, Field field) {
        super(attribute, attributeName, converter);
        this.field = field;
    }

    @Override
    public Object getPrivileged(Object bean) {
        return ReflectionHelper.getPrivileged(field, bean);
    }

    @Override
    public void setPriviliged(Object bean, Object value) {
        ReflectionHelper.setPrivileged(field, bean, value);
    }

}
