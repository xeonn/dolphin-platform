package com.canoo.dolphin.server.impl.info;

import com.canoo.dolphin.server.impl.Converters;
import org.opendolphin.core.Attribute;

import java.beans.PropertyDescriptor;

public class InterfacePropertyInfo extends PropertyInfo {

    private final PropertyDescriptor propertyDescriptor;

    public InterfacePropertyInfo(Attribute attribute, String attributeName, Converters.Converter converter, PropertyDescriptor propertyDescriptor) {
        super(attribute, attributeName, converter);
        this.propertyDescriptor = propertyDescriptor;
    }

    @Override
    public Object getPrivileged(Object bean) {
        return propertyDescriptor.getValue(getAttributeName());
    }

    @Override
    public void setPriviliged(Object bean, Object value) {
        propertyDescriptor.setValue(getAttributeName(), value);
    }
}
