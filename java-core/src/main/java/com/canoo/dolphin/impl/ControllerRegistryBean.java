package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ControllerRegistryBean {

    private Property<String> controllerName;

    private Property<String> controllerid;

    private Property<String> modelId;

    public String getControllerName() {
        return controllerName.get();
    }

    public void setControllerName(String controllerName) {
        this.controllerName.set(controllerName);
    }

    public String getControllerid() {
        return controllerid.get();
    }

    public void setControllerid(String controllerid) {
        this.controllerid.set(controllerid);
    }

    public String getModelId() {
        return modelId.get();
    }

    public void setModelId(String modelId) {
        this.modelId.set(modelId);
    }
}
