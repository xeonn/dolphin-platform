package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean(PlatformConstants.CONTROLLER_DESTROY_BEAN_NAME)
public class ControllerDestroyBean {

    private Property<String> controllerId;

    public String getControllerId() {
        return controllerId.get();
    }

    public void setControllerId(String controllerId) {
        this.controllerId.set(controllerId);
    }
}
