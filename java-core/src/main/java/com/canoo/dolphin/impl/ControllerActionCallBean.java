package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class ControllerActionCallBean {

    private Property<String> controllerid;

    private Property<String> actionName;

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
}
