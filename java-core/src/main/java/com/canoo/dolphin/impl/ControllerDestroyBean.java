package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ControllerDestroyBean {

    private Property<String> controllerid;

    public String getControllerid() {
        return controllerid.get();
    }

    public void setControllerid(String controllerid) {
        this.controllerid.set(controllerid);
    }
}
