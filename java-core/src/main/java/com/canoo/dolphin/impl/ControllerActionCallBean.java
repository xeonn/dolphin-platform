package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class ControllerActionCallBean {

    private Property<String> controllerid;

    private Property<String> actionName;

    public Property<String> getControllerid() {
        return controllerid;
    }

    public Property<String> getActionName() {
        return actionName;
    }
}
