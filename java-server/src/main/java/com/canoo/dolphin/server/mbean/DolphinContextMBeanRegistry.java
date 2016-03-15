package com.canoo.dolphin.server.mbean;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.mbean.beans.DolphinControllerInfo;
import com.canoo.dolphin.server.mbean.beans.DolphinControllerInfoMBean;
import com.canoo.dolphin.server.mbean.beans.DolphinSessionInfo;
import com.canoo.dolphin.server.mbean.beans.DolphinSessionInfoMBean;
import com.canoo.dolphin.server.mbean.beans.ModelProvider;
import com.canoo.dolphin.util.Assert;

public class DolphinContextMBeanRegistry {

    private final String dolphinContextId;

    public DolphinContextMBeanRegistry(String dolphinContextId) {
        this.dolphinContextId = Assert.requireNonNull(dolphinContextId, "dolphinContextId");
    }

    public Subscription registerDolphinContext(String dolphinSessionId) {
        Assert.requireNonBlank(dolphinSessionId, "dolphinSessionId");
        DolphinSessionInfoMBean mBean = new DolphinSessionInfo(dolphinSessionId);
        return MBeanRegistry.getInstance().register(mBean, new MBeanDescription("com.canoo.dolphin", "DolphinSession", "session"));
    }

    public Subscription registerController(Class<?> controllerClass, String controllerId, ModelProvider modelProvider) {
        Assert.requireNonNull(controllerClass, "controllerClass");
        Assert.requireNonBlank(controllerId, "controllerId");
        Assert.requireNonNull(modelProvider, "modelProvider");
        DolphinControllerInfoMBean mBean = new DolphinControllerInfo(dolphinContextId, controllerClass, controllerId, modelProvider);
        return MBeanRegistry.getInstance().register(mBean, new MBeanDescription("com.canoo.dolphin", controllerClass.getSimpleName(), "controller"));
    }

}
