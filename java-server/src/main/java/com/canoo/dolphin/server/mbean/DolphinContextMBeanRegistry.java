package com.canoo.dolphin.server.mbean;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.mbean.beans.DolphinControllerInfo;
import com.canoo.dolphin.server.mbean.beans.DolphinControllerInfoMBean;

import javax.management.JMException;

public class DolphinContextMBeanRegistry {

    private final String dolphinContextId;

    public DolphinContextMBeanRegistry(String dolphinContextId) {
        this.dolphinContextId = dolphinContextId;
    }

    public Subscription registerController(Class<?> controllerClass, String controllerId) {
        try {
            DolphinControllerInfoMBean mBean = new DolphinControllerInfo(controllerClass, controllerId);
            return MBeanRegistry.getInstance().register(mBean, new MBeanDescription("com.canoo.dolphin", controllerClass.getSimpleName(), "controller"));
        } catch (JMException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not register!", e);
        }
    }

}
