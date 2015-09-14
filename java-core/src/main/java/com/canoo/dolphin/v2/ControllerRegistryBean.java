package com.canoo.dolphin.v2;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ControllerRegistryBean {

    private Property<String> controllerName;

    private Property<String> controllerid;

    public Property<String> getControllerName() {
        return controllerName;
    }

    public Property<String> getControllerid() {
        return controllerid;
    }
}
