package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ControllerActionCallParamBean {

    private Property value;

    private Property valueType;

    public Object getValue() {
        return value.get();
    }

    public void setValue(Object value) {
        this.value.set(value);
    }

    public Object getValueType() {
        return valueType.get();
    }

    public void setValueType(Object valueType) {
        this.valueType.set(valueType);
    }
}
