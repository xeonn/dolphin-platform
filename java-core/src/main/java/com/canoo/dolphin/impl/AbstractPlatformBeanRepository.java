package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.PlatformBeanRepository;

public abstract class AbstractPlatformBeanRepository implements PlatformBeanRepository {

    private ControllerActionCallBean controllerActionCallBean;

    @Override
    public ControllerActionCallBean getControllerActionCallBean() {
        if (controllerActionCallBean == null) {
            throw new IllegalStateException("ControllerActionCallBean was not initialized yet");
        }
        return controllerActionCallBean;
    }

    protected void setControllerActionCallBean(ControllerActionCallBean controllerActionCallBean) {
        if (controllerActionCallBean == null) {
            throw new NullPointerException("ControllerActionCallBean must not be null");
        }
        this.controllerActionCallBean = controllerActionCallBean;
    }
}
