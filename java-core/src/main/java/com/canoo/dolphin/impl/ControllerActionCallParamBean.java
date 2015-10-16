package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean(PlatformConstants.CONTROLLER_ACTION_CALL_PARAM_BEAN_NAME)
public class ControllerActionCallParamBean {

    private Property value;

    private Property valueType;

    private Property<String> actionId;

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

    public String getActionId() {
        return actionId.get();
    }

    public void setActionId(String actionId) {
        this.actionId.set(actionId);
    }


}
