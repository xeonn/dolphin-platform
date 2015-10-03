package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean(PlatformConstants.INVOCATION_ERROR_NAME_BEAN)
public class ControllerActionCallErrorBean {

    private Property<String> controllerid;

    private Property<String> actionName;

    private Property<String> actionId;

    public String getControllerid() {
        return controllerid.get();
    }

    public void setControllerid(String controllerid) {
        this.controllerid.set(controllerid);
    }

    public String getActionName() {
        return actionName.get();
    }

    public void setActionName(String actionName) {
        this.actionName.set(actionName);
    }

    public void setActionId(String actionId) {
        this.actionId.set(actionId);
    }

    public String getActionId() {
        return actionId.get();
    }
}
