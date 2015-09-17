package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ControllerRegistryBean {

    private Property<String> controllerName;

    private Property<String> controllerid;

    private Property<String> modelId;

    public Property<String> getControllerName() {
        return controllerName;
    }

    public Property<String> getControllerid() {
        return controllerid;
    }

    public Property<String> getModelId() {
        return modelId;
    }
}
