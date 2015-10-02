package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ControllerRegistryBean {

    private Property<String> controllerName;

    private Property<String> controllerId;

    private Property<Object> model;

    public String getControllerName() {
        return controllerName.get();
    }

    public void setControllerName(String controllerName) {
        this.controllerName.set(controllerName);
    }

    public String getControllerId() {
        return controllerId.get();
    }

    public void setControllerId(String controllerId) {
        this.controllerId.set(controllerId);
    }

    public Object getModel() {
        return model.get();
    }

    public void setModel(Object model) {
        this.model.set(model);
    }
}
