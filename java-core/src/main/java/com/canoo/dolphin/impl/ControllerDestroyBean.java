package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class ControllerDestroyBean {

    private Property<String> controllerid;

    public String getControllerid() {
        return controllerid.get();
    }

    public void setControllerid(String controllerid) {
        this.controllerid.set(controllerid);
    }
}
