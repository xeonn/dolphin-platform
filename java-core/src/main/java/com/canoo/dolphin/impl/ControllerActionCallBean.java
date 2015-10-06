package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ControllerActionCallBean {

    private Property<String> controllerId;

    private Property<String> actionName;

    public String getControllerId() {
        return controllerId.get();
    }

    public void setControllerId(String controllerId) {
        this.controllerId.set(controllerId);
    }

    public String getActionName() {
        return actionName.get();
    }

    public void setActionName(String actionName) {
        this.actionName.set(actionName);
    }
}
